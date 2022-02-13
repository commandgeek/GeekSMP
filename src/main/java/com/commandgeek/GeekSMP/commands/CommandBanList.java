package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CommandBanList implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.banlist") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }
        new MessageManager("banlist-header").send(sender);
        Set<String> keys = Main.banned.getKeys(false);
        boolean empty = true;
        for (String key : keys) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(key));
            if (op.hasPlayedBefore()) {
                new MessageManager("banlist-list").replace("%player%", op.getName()).send(sender);
                empty = false;
            }
        }
        if (empty)
            new MessageManager("banlist-empty").send(sender);
        return true;
    }
}
