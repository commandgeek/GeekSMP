package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TabUntrust implements TabCompleter {
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (!(sender instanceof Player player)) {
            return results;
        }

        if (args.length == 1) {
            List<String> trusted = Main.trusted.getStringList(player.getUniqueId().toString());
            for (String uuid : trusted) {
                suggestions.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
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
