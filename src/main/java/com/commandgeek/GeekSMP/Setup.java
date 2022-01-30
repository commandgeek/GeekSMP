package com.commandgeek.GeekSMP;

import com.commandgeek.GeekSMP.managers.*;
import com.commandgeek.GeekSMP.menus.JoinMenu;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import org.javacord.api.entity.user.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Setup {

    public static void reload() {

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "GeekSMP Reload / Setup");

        Main.config = ConfigManager.loadConfig("config.yml");
        Main.messages = ConfigManager.loadConfig("messages.yml");
        Main.info = ConfigManager.loadConfig("info.yml");
        Main.stats = ConfigManager.loadData("stats.yml");
        Main.morphs = ConfigManager.loadData("morphs.yml");
        Main.alive = ConfigManager.loadData("alive.yml");
        Main.muted = ConfigManager.loadData("muted.yml");
        Main.banned = ConfigManager.loadData("banned.yml");
        Main.linked = ConfigManager.loadData("linked.yml");
        Main.locked = ConfigManager.loadData("locked.yml");
        Main.trusted = ConfigManager.loadData("trusted.yml");
        Main.pets = ConfigManager.loadData("pets.yml");

        DiscordManager.mutedRole = DiscordManager.roleFromConfig(Main.config, "discord.muted-role");
        DiscordManager.linkedRole = DiscordManager.roleFromConfig(Main.config, "discord.linked-role");
        DiscordManager.linkChannels = DiscordManager.channelsFromConfig(Main.config, "discord.link-channels");
        DiscordManager.changeLogChannel = DiscordManager.channelFromConfig(Main.config, "discord.change-log-channel");
        DiscordManager.smpChatChannel = DiscordManager.channelFromConfig(Main.config, "discord.smp-chat-channel");

        Main.bannedWords = Main.config.getStringList("settings.banned-words");

        LockManager.check();

        updateTabMetaForAll();
        updateTeams();
        updateAllRoles();

        Bukkit.getScheduler().cancelTasks(Main.instance);
        updateTasks();
        initializeMovementCheck();
    }

    public static void updateTeams() {
        ConfigurationSection section = Main.config.getConfigurationSection("groups");
        if (section != null) {
            Set<String> keys = section.getKeys(false);
            int i = 1;
            int length = NumberManager.length(keys.size());
            for (String key : keys) {
                TeamManager tm = new TeamManager(NumberManager.digits(i, length) + "_" + key);
                if (section.contains(key + ".color")) {
                    tm.color(ChatColor.valueOf(section.getString(key + ".color")));
                }
                if (section.contains(key + ".prefix")) {
                    String prefix = section.getString(key + ".prefix");
                    if (prefix != null) {
                        tm.prefix(ChatColor.translateAlternateColorCodes('&', prefix));
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
                String value = Main.morphs.getString(key);
                if (value != null) {
                    UUID uuid = UUID.fromString(value);
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity instanceof Zombie)
                        Morph.zombieTask(player);
                    if (entity instanceof Skeleton)
                        Morph.skeletonTask(player);
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
            Set<String> keys = section.getKeys(false);

            user.updateNickname(DiscordManager.server, player.getName());
            updateMemberRoles(player.getUniqueId());

            for (String key : keys) {
                if (section.contains(key + ".role")) {
                    String id = section.getString(key + ".role");
                    if (DiscordManager.userHasRole(user, id)) {
                        if (MorphManager.isMorphedPlayer(player)) {
                            new BukkitRunnable() {
                                public void run() {
                                    new MorphManager(player).unmorph();
                                }
                            }.runTaskLater(Main.instance, 0);
                        }
                        new TeamManager(TeamManager.endsWith(key)).join(player);
                        TeamManager.revive(player);
                        return;
                    }
                }
            }
        }

        // Check Revived
        if (TeamManager.isAlive(player)) {
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
        if (MuteManager.isMuted(uuid) || BanManager.isBanned(uuid)) user.addRole(DiscordManager.mutedRole);
        if (!MuteManager.isMuted(uuid) && !BanManager.isBanned(uuid)) user.removeRole(DiscordManager.mutedRole);
    }

    public static void updateAllRoles() {
        for (Player player : notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())) {
            updatePlayerRole(player);
        }
    }

    public static void join(Player player) {
        updatePlayerRole(player);
        if (!player.hasPlayedBefore())
            player.teleport(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
        EntityManager.hidePlayerForAll(player);
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1, 2);
        if (player.getLocation().getBlock().getType() == Material.NETHER_PORTAL)
            player.teleport(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
        JoinMenu.open(player);
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
        if (Main.config.contains("tab-meta.header")) {
            List<String> items = Main.config.getStringList("tab-meta.header");
            StringBuilder header = new StringBuilder();
            for (String item : items) {
                item = item.replaceAll("%tps%", notNull(Lag.getTPSString()));
                item = item.replaceAll("%online%", notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())));
                item = item.replaceAll("%max%", String.valueOf(Bukkit.getMaxPlayers()));
                header.append(item).append("\n");
            }
            player.setPlayerListHeader(ChatColor.translateAlternateColorCodes('&', header.toString().replaceAll("\n$", "")));
        }
        if (Main.config.contains("tab-meta.footer")) {
            List<String> items = Main.config.getStringList("tab-meta.footer");
            StringBuilder footer = new StringBuilder();
            for (String item : items) {
                item = item.replaceAll("%tps%", notNull(Lag.getTPSString()));
                item = item.replaceAll("%online%", notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())));
                item = item.replaceAll("%max%", String.valueOf(Bukkit.getMaxPlayers()));
                footer.append(item).append("\n");
            }
            player.setPlayerListFooter(ChatColor.translateAlternateColorCodes('&', footer.toString().replaceAll("\n$", "")));
        }
    }

    public static void updateTabMetaForAll() {
        for (Player online : notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())) {
            updateTabMeta(online);
        }
    }

    public static long lastDiscordChannelTopicUpdate = 0;
    public static void discordChannelTopicUpdate() {
        long time = new Timestamp(System.currentTimeMillis()).getTime();
        if (Math.abs(lastDiscordChannelTopicUpdate - time) > 300) {
            if (DiscordManager.smpChatChannel.asServerTextChannel().isPresent()) {
                DiscordManager.smpChatChannel.asServerTextChannel().get().updateTopic("**Online Players:** " + notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())) + "/" + Bukkit.getMaxPlayers());
                lastDiscordChannelTopicUpdate = time;
            }
        }
    }

    public static void initializeMovementCheck() {
        new BukkitRunnable() {
            public void run() {
                for (Player online : notNull(Integer.toString(OnlinePlayers.getOnlinePlayers().size())) {
                    AfkManager.update(online);
                }
            }
        }.runTaskTimer(Main.instance, 100, 100);
    }
}