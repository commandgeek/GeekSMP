package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.LinkManager;
import com.commandgeek.geeksmp.managers.MessageManager;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnlink implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.unlink")) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (sender instanceof Player player && args.length == 0) {
            LinkManager.unlink(player.getUniqueId());
            new MessageManager("linking.unlink.success").send(player);
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
            LinkManager.unlink(target.getUniqueId());
            new MessageManager("linking.unlink.other-success")
                    .replace("%player%", target.getName())
                    .send(sender);
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
