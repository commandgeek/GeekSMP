package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.EntityManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpy implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.spy") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        String tag = "spy-direct-messages";
        if (EntityManager.hasScoreboardTag(player, tag)) {
            player.removeScoreboardTag(tag);
            new MessageManager("spy-disabled").send(player);
        } else {
            player.addScoreboardTag(tag);
            new MessageManager("spy-enabled").send(player);
        }
        return true;
    }
}
