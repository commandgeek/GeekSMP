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
import java.util.UUID;


public class CommandReason implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.reason") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer target = EntityManager.getOfflinePlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player")
                        .replace("%player%", args[0])
                        .send(sender);
                return true;
            }
            long muteTime = MuteManager.checkMuted(target.getUniqueId().toString());
            long banTime = BanManager.checkBanned(target.getUniqueId().toString());

            if (!MuteManager.isMuted(target.getUniqueId()) && !BanManager.isBanned(target.getUniqueId())) {
                new MessageManager("reason-fail")
                        .replace("%player%", target.getName())
                        .send(sender);
            } else {
                if (MuteManager.isMuted(target.getUniqueId())) {
                    if (muteTime == -1) {
                        new MessageManager("reason-mute-permanent")
                                .replace("%player%", target.getName())
                                .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                .send(sender);
                    } else {
                        new MessageManager("reason-mute-temporary")
                                .replace("%player%", target.getName())
                                .replace("%duration%", NumberManager.getTimeFrom(muteTime))
                                .replace("%reason%", MuteManager.getReason(target.getUniqueId()))
                                .send(sender);
                    }
                }
                if (BanManager.isBanned(target.getUniqueId())) {
                    if (banTime == -1) {
                        new MessageManager("reason-ban-permanent")
                                .replace("%player%", target.getName())
                                .replace("%reason%", BanManager.getReason(target.getUniqueId()))
                                .send(sender);
                    } else {
                        new MessageManager("reason-ban-temporary")
                                .replace("%player%", target.getName())
                                .replace("%duration%", NumberManager.getTimeFrom(banTime))
                                .replace("%reason%", BanManager.getReason(target.getUniqueId()))
                                .send(sender);
                    }
                }
            }
            return true;
        }

        new MessageManager("invalid-arguments").send(sender);
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();
        if (args.length == 1) {
            for (String muted : Main.muted.getKeys(false)) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(muted));
                suggestions.add(op.getName());
            }
            for (String banned : Main.banned.getKeys(false)) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(banned));
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
