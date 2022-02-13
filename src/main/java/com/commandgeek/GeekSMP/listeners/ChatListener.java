package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;

@SuppressWarnings({"unused"})
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
                new MessageManager("chat-muted-temporary").replace("%duration%", NumberManager.getTimeFrom(remainder)).send(player);
            } else {
                new MessageManager("chat-muted-permanent").send(player);
            }
            event.setCancelled(true);
            return;
        }


        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            String name = team.getName().replaceAll("^[0-9]+_", "");
            if (ChatManager.setChatMessageFromFormat(event, name)) return;
        }

        if (EntityManager.hasScoreboardTag(player, "bypass-chat-allowed")) {
            String name = Main.config.getString("groups." + TeamManager.getLast() + ".revive-group");
            ChatManager.setChatMessageFromFormat(event, name);
        } else {
            event.setCancelled(true);
            new MessageManager("chat-forbidden").send(player);
        }
    }
}
