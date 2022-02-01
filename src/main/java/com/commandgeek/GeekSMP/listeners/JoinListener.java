package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Setup;
import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static com.commandgeek.GeekSMP.managers.EntityManager.checkHiddenPlayers;

@SuppressWarnings({"unused"})
public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Setup.discordChannelTopicUpdate();
        Player player = event.getPlayer();
        Setup.updateTabMetaForAll();

        StatsManager.add("joins");
        if (!player.hasPlayedBefore()) StatsManager.add("unique-joins");

        Setup.updatePlayerRole(player);
        checkHiddenPlayers(player);

        if (TeamManager.isRevived(player)) {
            new BukkitRunnable() {
                public void run() {
                    new MessageManager("revived-warning").send(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1, 1);
                }
            }.runTaskLater(Main.instance, 60);
        }
        if (TeamManager.isUndead(player)) {
            if (Main.morphs.contains(player.getUniqueId().toString())) {
                Main.morphs.set(player.getUniqueId().toString(), null);
                ConfigManager.saveData("morphs.yml", Main.morphs);
            }
            player.setGameMode(GameMode.SURVIVAL);
            event.setJoinMessage(null);
            Setup.join(player);
            return;
        }
        event.setJoinMessage(new MessageManager("join").replace("%player%", player.getName()).string());
        new MessageManager("smp-chat-join")
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
                reason = new MessageManager("login-banned-temporary")
                        .replace("%duration%", NumberManager.getTimeFrom(remainder))
                        .replace("%reason%", BanManager.getReason(player.getUniqueId())).string();
            } else {
                reason = new MessageManager("login-banned-permanent")
                        .replace("%reason%", BanManager.getReason(player.getUniqueId())).string();
            }
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, reason);
        }
    }

}
