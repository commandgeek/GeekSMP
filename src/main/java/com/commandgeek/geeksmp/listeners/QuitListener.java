package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        new PacketManager().removePlayer(player);

        new BukkitRunnable() {
            public void run() {
                Setup.updateTabMetaForAll();
                Setup.updateSetupTimer();
            }
        }.runTaskLater(Main.instance, 5);

        //noinspection ConstantConditions
        new MessageManager("discord.smp-chat.leave")
                .replace("%prefix%", ChatColor.stripColor(TeamManager.getPlayerTeam(player).getPrefix()))
                .replace("%player%", player.getName(), true)
                .sendDiscord(DiscordManager.smpChatChannel);

        if (TeamManager.getPlayerTeam(player) != null) {
            //noinspection ConstantConditions
            event.setQuitMessage(new MessageManager("join-leave.leave")
                    .replace("%prefix%", TeamManager.getPlayerTeam(player).getPrefix())
                    .replace("%player%", player.getName())
                    .string());

            if (TeamManager.isUndead(player)) {
                MorphManager.unmorph(player, false);
            }
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setReason(new MessageManager("join-leave.kicked")
                .replace("%reason%", event.getReason())
                .string());
    }
}
