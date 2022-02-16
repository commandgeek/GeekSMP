package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.LockManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLockTool implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Check if a player issued the command
        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }

        // Check if they have the permission node
        if (!player.hasPermission("geeksmp.command.locktool")) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        // If a player is specified, give lock tool to them instead of sender
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                target.getInventory().addItem(LockManager.getLockTool());
                new MessageManager("get-lock-tool").replace("%player%", player.getName()).send(target);
                new MessageManager("give-lock-tool").replace("%player%", args[0]).send(sender);
                return true;
            }
        }

        // Give lock tool to sender
        player.getInventory().addItem(LockManager.getLockTool());
        new MessageManager("get-lock-tool").replace("%player%", player.getName()).send(sender);
        return true;
    }
}
