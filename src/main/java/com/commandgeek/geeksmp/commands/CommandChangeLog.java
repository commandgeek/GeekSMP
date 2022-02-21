package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.ChangeLogManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CommandChangeLog implements TabExecutor {
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

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("create");
            suggestions.add("view");
            suggestions.add("remove");
            suggestions.add("add");
            suggestions.add("publish");
        }

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(suggestion);
            }
        }
        return results;
    }
}
