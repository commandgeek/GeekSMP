package com.commandgeek.geeksmp.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;


public class TabDebug implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("item0");
            suggestions.add("item27");
            suggestions.add("skeletonbow");
            suggestions.add("skeletonarrow");
        }

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(suggestion);
            }
        }
        return results;
    }
}
