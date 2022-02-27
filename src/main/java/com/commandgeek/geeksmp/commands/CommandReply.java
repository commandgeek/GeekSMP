package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReply implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.reply") || TeamManager.isUndead(player) || MuteManager.isMuted(player.getUniqueId())) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (!ChatManager.lastMessagedPlayer.containsKey(player)) {
            new MessageManager("direct-message.reply-failed").send(player);
            return true;
        }

        if (args.length >= 1) {
            Player receiver = ChatManager.lastMessagedPlayer.get(player);
            //noinspection ConstantConditions
            if (receiver.getPlayer().isOnline()) {
                ChatManager.directMessage(player, receiver, ChatManager.joinArguments(args, 0));
            } else {
                new MessageManager("direct-message.reply-failed").send(player);
            }
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(player);
        return true;
    }
}
