package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandTrash implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }

        if (!player.hasPermission("geeksmp.command.trash")) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        player.openInventory(Bukkit.getServer().createInventory(player, 36, ChatColor.translateAlternateColorCodes('&', "&5Trash")));
        return true;
        }
}
