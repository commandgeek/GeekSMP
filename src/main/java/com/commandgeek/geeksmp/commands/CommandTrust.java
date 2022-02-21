package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class CommandTrust implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.trust")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer target = EntityManager.getOfflinePlayer(args[0]);
            if (target == null) {
                new MessageManager("errors.invalid-player")
                        .replace("%player%", args[0])
                        .send(player);
                return true;
            }
            LockManager.trust(target, player);
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(player);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
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
