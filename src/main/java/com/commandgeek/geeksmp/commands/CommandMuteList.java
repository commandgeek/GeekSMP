package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CommandMuteList implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.mutelist") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }
        new MessageManager("mutelist-header").send(sender);
        Set<String> keys = Main.muted.getKeys(false);
        boolean empty = true;
        for (String key : keys) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(key));
            if (op.hasPlayedBefore()) {
                new MessageManager("mutelist-list").replace("%player%", op.getName()).send(sender);
                empty = false;
            }
        }
        if (empty)
            new MessageManager("mutelist-empty").send(sender);
        return true;
    }
}
