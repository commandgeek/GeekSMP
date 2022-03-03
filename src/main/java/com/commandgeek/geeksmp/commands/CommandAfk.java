package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.AfkManager;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAfk implements CommandExecutor {
    public final HashMap<String, Long> cooldowns = new HashMap<>();
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }

        if (!player.hasPermission("geeksmp.command.afk")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        int cooldownTime = 5; // Get number of seconds from wherever you want
        if (cooldowns.containsKey(sender.getName())) {
            long secondsLeft = ((cooldowns.get(sender.getName()) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
            if (secondsLeft > 0) {
                // Still cooling down
                new MessageManager("errors.cooldown")
                        .replace("%remaining%", String.valueOf(secondsLeft))
                        .send(player);
                return true;
            }
        }
        // No cooldown found or cooldown has expired, save new cooldown
        cooldowns.put(sender.getName(), System.currentTimeMillis());

        AfkManager.toggle(player);
        return true;
    }
}
