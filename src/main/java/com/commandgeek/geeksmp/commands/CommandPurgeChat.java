package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPurgeChat implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.purgechat") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.sendMessage("\n ".repeat(600) + new MessageManager("plugin.purge-chat").string());
        }
        return true;
    }
}
