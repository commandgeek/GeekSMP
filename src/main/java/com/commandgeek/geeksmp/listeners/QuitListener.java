package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        new BukkitRunnable() {
            public void run() {
                Setup.updateTabMetaForAll();
                Setup.updateSetupTimer();
            }
        }.runTaskLater(Main.instance, 1);

        Player player = event.getPlayer();
        new PacketManager().removePlayer(player);
        new MorphManager(player).unmorph(false);

        if (TeamManager.isUndead(player)) {
            event.setQuitMessage(new MessageManager("undead-leave").replace("%player%", player.getName()).string());
            return;
        }
        event.setQuitMessage(new MessageManager("leave").replace("%player%", player.getName()).string());
        new MessageManager("smp-chat-leave")
                .replace("%player%", player.getName(), true)
                .sendDiscord(DiscordManager.smpChatChannel);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setReason(new MessageManager("logout-kicked").replace("%reason%", event.getReason()).string());
    }
}