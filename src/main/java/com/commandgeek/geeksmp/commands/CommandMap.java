package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMap implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.map")) {
            new MessageManager("no-permission").send(player);
            return true;
        }
        new MessageManager("link-map").send(sender);
        return true;
    }
}
