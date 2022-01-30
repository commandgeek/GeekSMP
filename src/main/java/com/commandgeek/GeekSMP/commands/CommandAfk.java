package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.AfkManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandAfk implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
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
