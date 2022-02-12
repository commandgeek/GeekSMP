package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.AfkManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
        List<String> disabled = Main.disabledCommands;

        String commandName = args[0].toLowerCase(Locale.ROOT).replace("minecraft:", "");
        if (disabled.contains(commandName.replace("/", "")) || disabled.contains(commandName)) {
            new MessageManager("disabled-command").send(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
