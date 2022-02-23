package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.MuteManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CommandUnmute implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.unlink") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            MuteManager.unmute(args[0], sender);
            return true;
        }
        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            for (String key : Main.muted.getKeys(false)) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(key));
                suggestions.add(op.getName());
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
