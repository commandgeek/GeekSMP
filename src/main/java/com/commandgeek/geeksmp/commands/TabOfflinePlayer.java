package com.commandgeek.geeksmp.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabOfflinePlayer implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            if (args[0].length() == 0) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    suggestions.add(online.getName());
                }
            } else {
                for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                    suggestions.add(offline.getName());
                }
            }
        }
        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(suggestion);
            }
        }
        return results;
    }
}
