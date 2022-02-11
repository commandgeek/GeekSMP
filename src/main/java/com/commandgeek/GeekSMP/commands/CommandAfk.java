package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.AfkManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandAfk implements CommandExecutor {
    public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }

        if (!player.hasPermission("geeksmp.command.afk")) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        int cooldownTime = 5; // Get number of seconds from wherever you want
        if(cooldowns.containsKey(sender.getName())) {
            long secondsLeft = ((cooldowns.get(sender.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
            if(secondsLeft>0) {
                // Still cooling down
                sender.sendMessage("You cant use that commands for another "+ secondsLeft +" seconds!");
                return true;
            }
        }
        // No cooldown found or cooldown has expired, save new cooldown
        cooldowns.put(sender.getName(), System.currentTimeMillis());
        if (!(sender instanceof Player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }

        if (!player.hasPermission("geeksmp.command.afk")) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        AfkManager.toggle(player);
        return true;
        }
}
