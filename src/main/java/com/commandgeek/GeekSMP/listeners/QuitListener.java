package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Setup;
import com.commandgeek.GeekSMP.managers.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unused"})
public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            public void run() {
                Setup.tabUpdate();
                Setup.updateSetupTimer();
            }
        }.runTaskLater(Main.instance, 1);

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
