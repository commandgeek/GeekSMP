package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.managers.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;


public class ChatListener implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        AfkManager.moved(player);
        if (AfkManager.check(player)) {
            AfkManager.disable(player);
        }

        // Check Muted
        long remainder = MuteManager.checkMuted(String.valueOf(player.getUniqueId()));
        if (remainder != 0) {
            if (remainder > 0) {
                new MessageManager("punishing.muting.mute.chat.temporary").replace("%duration%", NumberManager.getTimeFrom(remainder)).send(player);
            } else {
                new MessageManager("punishing.muting.mute.chat.permanent").send(player);
            }
            event.setCancelled(true);
            return;
        }

        Team team = TeamManager.getPlayerTeam(player);
        if (team != null && !ChatManager.setChatMessageFromFormat(event, team.getName().replaceAll("^[0-9]+_", ""))) {
            event.setCancelled(true);
            new MessageManager("chat.forbidden").send(player);
        }
    }
}
