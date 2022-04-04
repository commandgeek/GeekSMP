package com.commandgeek.geeksmp;

import com.commandgeek.geeksmp.managers.*;
import com.commandgeek.geeksmp.menus.JoinMenu;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.metadata.MetadataValue;
import org.javacord.api.entity.user.User;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;


public class Setup {
    public static void reload() {
        // Discord roles
        DiscordManager.mutedRole = DiscordManager.roleFromConfig(Main.config, "discord.muted-role");
        DiscordManager.linkedRole = DiscordManager.roleFromConfig(Main.config, "discord.linked-role");
        DiscordManager.linkChannels = DiscordManager.channelsFromConfig(Main.config, "discord.link-channels");
        DiscordManager.changeLogChannel = DiscordManager.channelFromConfig(Main.config, "discord.change-log-channel");
        DiscordManager.smpChatChannel = DiscordManager.channelFromConfig(Main.config, "discord.smp-chat-channel");

        // Cancel tasks & setup message
        Bukkit.getScheduler().cancelTasks(Main.instance);
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "GeekSMP finished setup");

        // Set spawn protection
        Bukkit.setSpawnRadius(Main.config.getInt("settings.spawn-protection"));

        // One-time updates
        loadFiles();
        updateTasks();
        updateTabMetaForAll();
        updateTeams();
        updateAllRoles();
        LockManager.check();

        // Constant updates
        tabUpdate();
        updateSetupTimer();
        initializeMovementCheck();
    }

    static void loadFiles() {
        Main.config = ConfigManager.loadConfig("config.yml");
        Main.lists = ConfigManager.loadConfig("lists.yml");
        Main.messages = ConfigManager.loadConfig("messages.yml");
        Main.info = ConfigManager.loadConfig("info.yml");
        Main.stats = ConfigManager.loadData("stats.yml");
        Main.morphs = ConfigManager.loadData("morphs.yml");
        Main.morphed = ConfigManager.loadData("morphed.yml");
        Main.alive = ConfigManager.loadData("alive.yml");
        Main.muted = ConfigManager.loadData("muted.yml");
        Main.banned = ConfigManager.loadData("banned.yml");
        Main.linked = ConfigManager.loadData("linked.yml");
        Main.locked = ConfigManager.loadData("locked.yml");
        Main.trusted = ConfigManager.loadData("trusted.yml");
        // DEPRECATED:
        Main.bypass = ConfigManager.loadData("bypass.yml");

        // Load pets file if pets are enabled
        if (MorphManager.pets()) {
            Main.pets = ConfigManager.loadData("pets.yml");
        }
    }


    public static void updateTeams() {
        ConfigurationSection section = Main.config.getConfigurationSection("groups");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            int i = 1;
            int length = NumberManager.length(keys.size());
            for (String key : keys) {
                TeamManager team = new TeamManager(NumberManager.digits(i, length) + "_" + key);
                if (section.contains(key + ".color")) {
                    team.color(ChatColor.valueOf(section.getString(key + ".color")));
                }
                if (section.contains(key + ".prefix")) {
                    String prefix = section.getString(key + ".prefix");
                    if (prefix != null) {
                        team.prefix(ChatColor.translateAlternateColorCodes('&', prefix));
                    }
                }
                i++;
            }
        }
    }

    public static void updateTasks() {
        for (String key : Main.morphs.getKeys(false)) {
            Player player = Bukkit.getPlayer(UUID.fromString(key));
            if (player != null) {
                if (Main.morphs.getString(key) != null) {
                    MorphManager.universalMorphTask(player, null);
                }
            }
        }
    }

    public static void updatePlayerRole(Player player) {

        // Check Should Change
        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            String name = team.getName().replaceAll("^[0-9]+_", "");
            if (Main.config.contains("groups." + name + ".status")) {
                String change = Main.config.getString("groups." + name + ".status");
                if (change != null) {
                    if (change.equalsIgnoreCase("owner")) {
                        return;
                    }
                }
            }
        }

        // Check Linked Roles
        User user = DiscordManager.getUserFromPlayer(player);
        ConfigurationSection section = Main.config.getConfigurationSection("groups");
        if (user != null && section != null) {
            user.updateNickname(DiscordManager.server, player.getName());
            updateMemberRoles(player.getUniqueId());

            for (String key : section.getKeys(false)) {
                if (DiscordManager.userHasRole(user, section.getString(key + ".role"))) {
                    if (MorphManager.isMorphedPlayer(player)) {
                        new BukkitRunnable() {
                            public void run() {
                                MorphManager.unmorph(player,true);
                            }
                        }.runTaskLater(Main.instance, 0);
                    }
                    new TeamManager(TeamManager.endsWith(key)).join(player);
                    TeamManager.revive(player);
                    return;
                }
            }
        }

        // Check Revived
        if (TeamManager.isAlive(player.getUniqueId().toString())) {
            String name = Main.config.getString("groups." + TeamManager.getLast() + ".revive-group");
            new TeamManager(TeamManager.endsWith(name)).join(player);
            return;
        }

        // Assign Undead Role
        new TeamManager(TeamManager.endsWith(TeamManager.getLast())).join(player);
    }

    public static void updateMemberRoles(UUID uuid) {
        User user = DiscordManager.getUserFromUuid(uuid);
        if (user == null) return;
        if (MuteManager.isMuted(uuid) || BanManager.isBanned(uuid)) {
            user.addRole(DiscordManager.mutedRole);
        }
        if (!MuteManager.isMuted(uuid) && !BanManager.isBanned(uuid)) {
            user.removeRole(DiscordManager.mutedRole);
        }
    }

    public static void updateAllRoles() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerRole(player);
        }
    }

    public static void join(Player player) {
        updatePlayerRole(player);
        updateTeams();
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 2);

        // Teleport to spawn if they haven't played before
        if (!player.hasPlayedBefore()) {
            player.teleport(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
        }

        // Teleport to spawn if they join in a nether portal
        if (player.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
            player.teleport(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
        }

        // Open morph menu if undead and don't have a morph
        if (!MorphManager.isMorphedPersistent(player)) {
            JoinMenu.open(player);
        }
    }

    public static void registerCommand(String name, CommandExecutor executor, TabCompleter completer) {
        PluginCommand command = Bukkit.getPluginCommand(name);
        if (command != null) {
            command.setExecutor(executor);
            command.setTabCompleter(completer);
        } else {
            Bukkit.getLogger().warning("Could not register command \"" + name + "\"");
        }
    }

    public static void updateTabMeta(Player player) {
        if (Main.config.contains("tab.header")) {
            List<String> items = Main.config.getStringList("tab.header");
            StringBuilder header = new StringBuilder();
            for (String item : items) {
                item = PlaceholderAPI.setPlaceholders(player, item);
                header.append(item).append("\n");
            }
            player.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&', header.toString().replaceAll("\n$", "")));
        }
        if (Main.config.contains("tab.footer")) {
            List<String> items = Main.config.getStringList("tab.footer");
            StringBuilder footer = new StringBuilder();
            for (String item : items) {
                item = PlaceholderAPI.setPlaceholders(player, item);
                footer.append(item).append("\n");
            }
            player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&', footer.toString().replaceAll("\n$", "")));
        }
    }

    // Update tab for every online player
    public static void updateTabMetaForAll() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            updateTabMeta(online);
        }
    }

    // Update the tab periodically for placeholders
    public static void tabUpdate() {
        new BukkitRunnable() {
            public void run() {
                updateTabMetaForAll();
            }
        }.runTaskTimer(Main.instance, 0, 20); // 20 ticks (1 second)
    }

    // Update Discord channel topic, teams, and roles periodically
    public static void updateSetupTimer() {
        new BukkitRunnable() {
            public void run() {
                updateTeams();
                updateAllRoles();
                if (DiscordManager.smpChatChannel.asServerTextChannel().isPresent() && Main.config.contains("discord.smp-chat-topic")) {
                    List<String> items = Main.config.getStringList("discord.smp-chat-topic");
                    StringBuilder topic = new StringBuilder();
                    for (String item : items) {
                        item = PlaceholderAPI.setPlaceholders(null, item);
                        item = ChatColor.stripColor(item);
                        topic.append(item).append("\n");
                    }
                    DiscordManager.smpChatChannel.asServerTextChannel().get().updateTopic(topic.toString().replaceAll("\n$", ""));
                }
            }
        }.runTaskTimer(Main.instance, 0, 12000); // 12000 ticks (10 minutes)
    }

    // Run AfkManager.update for everyone online periodically
    public static void initializeMovementCheck() {
        new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    AfkManager.update(online);
                }
            }
        }.runTaskTimer(Main.instance, 100, 100);
    }

    // Check if player is vanished
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }
}
