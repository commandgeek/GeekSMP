package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.LockManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLockTool implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Check if a player issued the command
        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }

        // Check if they have the permission node
        if (!player.hasPermission("geeksmp.command.locktool")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        // If a player is specified, give lock tool to them instead of sender
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                target.getInventory().addItem(LockManager.lockTool());
                new MessageManager("trusting.lock-tool.get").replace("%player%", player.getName()).send(target);
                new MessageManager("trusting.lock-tool.give").replace("%target%", args[0]).send(sender);
                return true;
            }
        }

        // Give lock tool to sender
        player.getInventory().addItem(LockManager.lockTool());
        new MessageManager("trusting.lock-tool.get").replace("%player%", player.getName()).send(sender);
        return true;
    }
}
