package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabTrust implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return results;
        }

        if (args.length == 1) {
            List<String> trusted = Main.trusted.getStringList(player.getUniqueId().toString());
            if (args[0].length() == 0) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!trusted.contains(online.getUniqueId().toString()) && online != player) {
                        suggestions.add(online.getName());
                    }
                }
            } else {
                for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
                    if (!trusted.contains(offline.getUniqueId().toString()) && offline.getUniqueId() != player.getUniqueId()) {
                        suggestions.add(offline.getName());
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
