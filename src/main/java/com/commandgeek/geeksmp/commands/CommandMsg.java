package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMsg implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.msg") || TeamManager.isUndead(player) || MuteManager.isMuted(player.getUniqueId())) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length >= 2) {
            ChatManager.directMessage(player, args[0], ChatManager.joinArguments(args, 1));
            return true;
        }

        new MessageManager("invalid-arguments").send(player);
        return true;
    }
}
