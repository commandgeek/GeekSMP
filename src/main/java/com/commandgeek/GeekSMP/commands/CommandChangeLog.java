package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.ChangeLogManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChangeLog implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.chang elog") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("create")) {
                ChangeLogManager.create(sender);
                ChangeLogManager.send(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("view")) {
                ChangeLogManager.send(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                ChangeLogManager.removeLastItem(sender);
                ChangeLogManager.send(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("publish")) {
                ChangeLogManager.publish(sender);
                return true;
            }
        }
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("add")) {
                StringBuilder string = new StringBuilder();
                for (int i = 1; i < args.length; i++) {
                    string.append(args[i]).append(" ");
                }
                ChangeLogManager.addItem(sender, string.toString().trim());
                ChangeLogManager.send(sender);
                return true;
            }
        }
        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
