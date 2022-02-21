package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.NumberManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandPlayerInfo implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.playerinfo")) {
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

            new MessageManager("information.player.info-header")
                    .replace("%player%", target.getName())
                    .send(sender);
            new MessageManager("information.player.info-item")
                    .replace("%key%", "First Joined")
                    .replace("%value%", NumberManager.getTimeSince(target.getFirstPlayed()))
                    .send(sender);
            new MessageManager("information.player.info-item")
                    .replace("%key%", "Last Joined")
                    .replace("%value%", target.isOnline() ? "Online" : NumberManager.getTimeSince(target.getLastPlayed()))
                    .send(sender);
            new MessageManager("information.player.info-item")
                    .replace("%key%", "UUID")
                    .replace("%value%", target.getUniqueId().toString())
                    .send(sender);
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
