package com.commandgeek.geeksmp;

import com.commandgeek.geeksmp.commands.*;
import com.commandgeek.geeksmp.listeners.*;
import com.commandgeek.geeksmp.listeners.discord.DiscordMessageCreateListener;
import com.commandgeek.geeksmp.managers.*;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.intent.Intent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Main extends JavaPlugin {
    public static Main instance;
    public static DiscordApi discordAPI;
    public static ProtocolManager protocolManager;
    public static String botPrefix;

    public static FileConfiguration config;
    public static FileConfiguration lists;
    public static FileConfiguration messages;
    public static FileConfiguration info;

    public static FileConfiguration stats;
    public static FileConfiguration morphs;
    public static FileConfiguration morphed;
    public static FileConfiguration alive;
    public static FileConfiguration muted;
    public static FileConfiguration banned;
    public static FileConfiguration linked;
    public static FileConfiguration locked;
    public static FileConfiguration trusted;
    public static FileConfiguration pets;
    // DEPRECATED:
    public static FileConfiguration bypass;

    @Override
    public void onEnable() {

        instance = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        ItemManager.init();
        // Startup message
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "==================");
        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + " GeekSMP Plugin");
//        getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + " by CommandGeek");
        getServer().getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "==================");

        // Register commands
        Setup.registerCommand("revive", new CommandRevive(), new TabPlayer());
        Setup.registerCommand("unrevive", new CommandUnrevive(), new TabPlayer());
        Setup.registerCommand("mute", new CommandMute(), new TabOfflinePlayer());
        Setup.registerCommand("mutelist", new CommandMuteList(), new TabEmpty());
        Setup.registerCommand("unmute", new CommandUnmute(), null);
        Setup.registerCommand("ban", new CommandBan(), new TabOfflinePlayer());
        Setup.registerCommand("banlist", new CommandBanList(), new TabEmpty());
        Setup.registerCommand("unban", new CommandUnban(), null);
        Setup.registerCommand("die", new CommandDie(), new TabEmpty());
        Setup.registerCommand("gmc", new CommandGmc(), new TabPlayer());
        Setup.registerCommand("gms", new CommandGms(), new TabPlayer());
        Setup.registerCommand("gma", new CommandGma(), new TabPlayer());
        Setup.registerCommand("gmsp", new CommandGmsp(), new TabPlayer());
        Setup.registerCommand("rules", new CommandRules(), new TabEmpty());
        Setup.registerCommand("info", new CommandInfo(), new TabEmpty());
        Setup.registerCommand("help", new CommandHelp(), new TabEmpty());
        Setup.registerCommand("code", new CommandCode(), new TabEmpty());
        Setup.registerCommand("discord", new CommandDiscord(), new TabEmpty());
        Setup.registerCommand("twitch", new CommandTwitch(), new TabEmpty());
        Setup.registerCommand("map", new CommandMap(), new TabEmpty());
        Setup.registerCommand("reloadsmp", new CommandReloadSmp(), new TabEmpty());
        Setup.registerCommand("changelog", new CommandChangeLog(), null);
        Setup.registerCommand("unlink", new CommandUnlink(), new TabOfflinePlayer());
        Setup.registerCommand("purgechat", new CommandPurgeChat(), new TabEmpty());
        Setup.registerCommand("purgeteams", new CommandPurgeTeams(), new TabEmpty());
        Setup.registerCommand("feed", new CommandFeed(), new TabPlayer());
        Setup.registerCommand("heal", new CommandHeal(), new TabPlayer());
        Setup.registerCommand("tphere", new CommandTpHere(), new TabPlayer());
        Setup.registerCommand("tp", new CommandTp(), new TabPlayer());
        Setup.registerCommand("msg", new CommandMsg(), new TabPlayer());
        Setup.registerCommand("reply", new CommandReply(), new TabEmpty());
        Setup.registerCommand("msgtoggle", new CommandMsgToggle(), new TabEmpty());
        Setup.registerCommand("spy", new CommandSpy(), new TabEmpty());
        Setup.registerCommand("track", new CommandTrack(), null);
        Setup.registerCommand("seeinv", new CommandSeeInv(), new TabPlayer());
        Setup.registerCommand("lookup", new CommandLookup(), new TabOfflinePlayer());
        Setup.registerCommand("playerinfo", new CommandPlayerInfo(), new TabOfflinePlayer());
        Setup.registerCommand("stats", new CommandStats(), new TabEmpty());
        Setup.registerCommand("trust", new CommandTrust(), null);
        Setup.registerCommand("trustlist", new CommandTrustList(), new TabOfflinePlayer());
        Setup.registerCommand("untrust", new CommandUntrust(), null);
        Setup.registerCommand("inspect", new CommandInspect(), new TabEmpty());
        Setup.registerCommand("ip", new CommandIP(), new TabPlayer());
        Setup.registerCommand("afk", new CommandAfk(), new TabEmpty());
        Setup.registerCommand("reason", new CommandReason(), null);
        Setup.registerCommand("bypass", new CommandBypass(), new TabEmpty());
        Setup.registerCommand("broadcast", new CommandBroadcast(), new TabEmpty());
        Setup.registerCommand("locktool", new CommandLockTool(), new TabPlayer());
        Setup.registerCommand("debug", new CommandDebug(), null);
        Setup.registerCommand("trash", new CommandTrash(), new TabEmpty());
        Setup.registerCommand("day", new CommandDay(), new TabWorld());
        Setup.registerCommand("night", new CommandNight(), new TabWorld());
        Setup.registerCommand("enderchest", new CommandEnderChest(), new TabPlayer());
        Setup.registerCommand("uptime", new CommandUptime(), new TabEmpty());

        // Create files
        ConfigManager.createDefaultConfig("config.yml");
        ConfigManager.createDefaultConfig("lists.yml");
        ConfigManager.createDefaultConfig("messages.yml");
        ConfigManager.createDefaultConfig("info.yml");
        ConfigManager.createData("stats.yml");
        ConfigManager.createData("morphs.yml");
        ConfigManager.createData("morphed.yml");
        ConfigManager.createData("alive.yml");
        ConfigManager.createData("muted.yml");
        ConfigManager.createData("banned.yml");
        ConfigManager.createData("linked.yml");
        ConfigManager.createData("locked.yml");
        ConfigManager.createData("trusted.yml");

        // Load files and register commands
        Setup.loadFiles();
        registerRecipes();

        // Pet stuff
        if (MorphManager.pets()) {
            Setup.registerCommand("pet", new CommandPet(), null);
            ConfigManager.createData("pets.yml");
            Main.pets = ConfigManager.loadData("pets.yml");
        } else {
            Setup.registerCommand("pet", new CommandPet(), new TabEmpty());
        }

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new QuitListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new MoveListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new InteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new CommandListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new BlockListener(), this);

        // Discord
        botPrefix = config.getString("discord.prefix");

        // Register Discord Bot
        new DiscordApiBuilder()
                .setToken(config.getString("discord.bot-token"))
                .setAllIntentsExcept(Intent.GUILD_PRESENCES, Intent.GUILD_MESSAGE_TYPING)
                .login()
                .thenAccept(this::onConnectToDiscord)
                .exceptionally(error -> {
                    getLogger().warning("Failed to connect to Discord!");
                    return null;
                });

        // Reload
        new BukkitRunnable() {
            public void run() {
                if (discordAPI != null) {
                    Setup.reload();
                    new MessageManager("discord.smp-chat.start").sendDiscord(DiscordManager.smpChatChannel);
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 1);
    }

    private void onConnectToDiscord(DiscordApi api) {
        Main.discordAPI = api;

        getLogger().info("Connected to Discord as " + api.getYourself().getDiscriminatedName());

        api.addListener(new DiscordMessageCreateListener());
        api.updateActivity(ActivityType.WATCHING, "GeekSMP");
    }

    @Override
    public void onDisable() {

        // Unregister Discord Bot
        if (discordAPI != null) {
            new MessageManager("discord.smp-chat.stop").sendDiscord(DiscordManager.smpChatChannel);
            discordAPI = null;
        }

        // Delete all Morphs
        for (Player online : Bukkit.getOnlinePlayers()) {
            MorphManager.unmorph(online, false);
            online.kickPlayer(ChatColor.RED + "Server stopping/restarting!");
        }

//        Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_PURPLE + "GeekSMP shutting down...");
        getServer().shutdown();
    }

    private void registerRecipes() {
        // Lock Tool
        if (config.getBoolean("recipes.lock-tool")) {
            new RecipeManager("lock_tool", LockManager.lockTool)
                    .shape(
                            "  A",
                            " S ",
                            "S  ")
                    .set('S', Material.STICK)
                    .set('A', Material.AMETHYST_SHARD)
                    .register();
        }

        // Glow Berries
        if (config.getBoolean("recipes.glow-berries")) {
            new RecipeManager("glow_berries", new ItemStack(Material.GLOW_BERRIES))
                    .shape(
                            " G ",
                            "GBG",
                            " G ")
                    .set('G', Material.GLOWSTONE_DUST)
                    .set('B', Material.SWEET_BERRIES)
                    .register();
        }

        // Heart of the Sea
        if (config.getBoolean("recipes.heart-of-the-sea")) {
            new RecipeManager("heart_of_the_sea", new ItemStack(Material.HEART_OF_THE_SEA))
                    .shape(
                            "LCL",
                            "CSC",
                            "LCL")
                    .set('L', Material.SEA_LANTERN)
                    .set('C', Material.PRISMARINE_CRYSTALS)
                    .set('S', Material.NETHER_STAR)
                    .register();
        }
        if (config.getBoolean("recipes.bad_omen_potion")) {
            new RecipeManager("Bad_Omen_Potion", new ItemStack(ItemManager.bad_omen))
                    .shape(
                            "L L",
                            " S ",
                            "L L")
                    .set('L', Material.EMERALD_BLOCK)
                    .set('S', Material.GOLD_INGOT)
                    .register();
        }
    }
}
