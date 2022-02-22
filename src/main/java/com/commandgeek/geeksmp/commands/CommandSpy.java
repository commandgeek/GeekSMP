package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpy implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.spy") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        String tag = "spy-direct-messages";
        if (EntityManager.hasScoreboardTag(player, tag)) {
            player.removeScoreboardTag(tag);
            new MessageManager("direct-message.spy.").send(player);
        } else {
            player.addScoreboardTag(tag);
            new MessageManager("direct-message.spy.").send(player);
        }
        return true;
    }
}
