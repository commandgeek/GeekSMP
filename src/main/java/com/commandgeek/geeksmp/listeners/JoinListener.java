package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            public void run() {
                Setup.updateTabMetaForAll();
                Setup.updateSetupTimer();
                if (TeamManager.isUndead(player)) {
                    if (Main.morphs.contains(player.getUniqueId().toString())) {
                        Main.morphs.set(player.getUniqueId().toString(), null);
                        ConfigManager.saveData("morphs.yml", Main.morphs);
                    }

                    // Assign selected morph
                    if (MorphManager.isMorphedPersistent(player)) {
                        MorphManager.morph(player, MorphManager.getEntityTypePersistent(player));
                    }

                    // Hide player for everyone else if morphed
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (MorphManager.isMorphedPlayer(online)) {
                            EntityManager.hidePlayerForAll(online);
                        }
                    }

                    Setup.join(player);
                    player.setGameMode(GameMode.ADVENTURE);
                    event.setJoinMessage(new MessageManager("join-leave.undead.join").replace("%player%", player.getName()).string());
                }
            }
        }.runTaskLater(Main.instance, 5);

        // Add unique joins stat
        if (!player.hasPlayedBefore()) {
            StatsManager.add("unique-joins");
        }

        // Enable lock bypass
        if (BypassManager.check(player)) {
            BypassManager.enable(player, false);
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            EntityManager.checkHiddenPlayer(online, player);
        }

        StatsManager.add("joins");
        Setup.updatePlayerRole(player);
        event.setJoinMessage(new MessageManager("join-leave.join").replace("%player%", player.getName()).string());
        new MessageManager("smp-chat.join")
                .replace("%player%", player.getName(), true)
                .sendDiscord(DiscordManager.smpChatChannel);
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (BanManager.isBanned(player.getUniqueId())) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5GeekSMP&8] &e" + player.getName() + " attempted to connect while banned. &6(" + player.getUniqueId() + ")"));
        } else if ((!player.isOp() && !player.isWhitelisted() && Bukkit.getServer().hasWhitelist())) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&5GeekSMP&8] &e" + player.getName() + " attempted to connect while not whitelisted. &6(" + player.getUniqueId() + ")"));
        }

        // Check Discord API
        if (Main.discordAPI == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.GREEN + "Loading Discord API");
        }

        long remainder = BanManager.checkBanned(player.getUniqueId().toString());
        if (remainder != 0) {
            String reason;
            if (remainder > 0) {
                reason = new MessageManager("punishing.banning.login.-temporary")
                        .replace("%duration%", NumberManager.getTimeFrom(remainder))
                        .replace("%reason%", BanManager.getReason(player.getUniqueId())).string();
            } else {
                reason = new MessageManager("punishing.banning.login.-permanent")
                        .replace("%reason%", BanManager.getReason(player.getUniqueId())).string();
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason);
        }
    }

}
