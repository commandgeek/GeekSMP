package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.managers.AfkManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;

@SuppressWarnings({"unused"})
public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        AfkManager.moved(player);
        if (AfkManager.check(player)) {
            AfkManager.disable(player);
        }

        String[] args = event.getMessage().split(" ");
        String[] disabled = {"/minecraft:help", "/minecraft:list", "/list", "/minecraft:me", "/me", "/minecraft:msg", "/minecraft:teammsg", "/teammsg", "/minecraft:tell", "/tell", "/minecraft:tm", "/tm", "/minecraft:trigger", "/trigger", "/minecraft:w", "/w"};


        if (Arrays.asList(disabled).contains(args[0].toLowerCase())) {
            new MessageManager("disabled-command").send(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
