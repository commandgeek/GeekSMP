/*package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MorphManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabPet implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return results;
        }

        if (args.length == 1) {
            suggestions.add("add");
            suggestions.add("remove");
            suggestions.add("list");
            suggestions.add("clear");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                if (args[1].length() == 0) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (!MorphManager.isPettedBy(online, player)) {
                            suggestions.add(online.getName());
                        }
                    }
                } else {
                    for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                        if (!MorphManager.isPettedBy(offline, player)) {
                            suggestions.add(offline.getName());
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                for (String key : Main.pets.getKeys(false)) {
                    List<String> owners = Main.pets.getStringList(key);
                    if (owners.contains(player.getUniqueId().toString())) {
                        suggestions.add(Bukkit.getOfflinePlayer(UUID.fromString(key)).getName());
                    }
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
*/
