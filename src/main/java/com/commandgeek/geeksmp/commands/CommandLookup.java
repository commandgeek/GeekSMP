package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.DiscordManager;
import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.javacord.api.entity.user.User;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLookup implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.lookup")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            OfflinePlayer target = EntityManager.getOfflinePlayer(args[0]);
            if (target == null) {
                new MessageManager("errors.invalid-player")
                        .replace("%player%", args[0])
                        .send(sender);
                return true;
            }

            User user = DiscordManager.getUserFromUuid(target.getUniqueId());
            if (user == null) {
                new MessageManager("lookup.missing")
                        .replace("%player%", target.getName())
                        .send(sender);
                } else {
                new MessageManager("lookup.player")
                        .replace("%player%", target.getName())
                        .replace("%user%", user.getDiscriminatedName())
                        .send(sender);
            }
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
