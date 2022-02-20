package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.AfkManager;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Locale;


public class CommandListener implements Listener {

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        // Un-AFK
        AfkManager.moved(player);
        if (AfkManager.check(player)) {
            AfkManager.disable(player);
        }

        // Disabled command
        String[] args = event.getMessage().split(" ");
        List<String> disabled = Main.lists.getStringList("disabled-commands");
        String commandName = args[0].toLowerCase(Locale.ROOT);
        if (disabled.contains(commandName.replace("/", "")) || disabled.contains(commandName)) {
            new MessageManager("disabled-command").send(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
