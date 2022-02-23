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

public class CommandBanList implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.banlist") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }
        new MessageManager("punishing.banning.list.header").send(sender);
        Set<String> keys = Main.banned.getKeys(false);
        boolean empty = true;
        for (String key : keys) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(key));
            if (op.hasPlayedBefore()) {
                new MessageManager("punishing.banning.list.list").replace("%player%", op.getName()).send(sender);
                empty = false;
            }
        }
        if (empty)
            new MessageManager("punishing.banning.list.empty").send(sender);
        return true;
    }
}
