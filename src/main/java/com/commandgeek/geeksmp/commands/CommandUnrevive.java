package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.MorphManager;
import com.commandgeek.geeksmp.managers.TeamManager;
import com.commandgeek.geeksmp.menus.JoinMenu;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnrevive implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.unrevive")) {
            new MessageManager("errors.no-permission").send(sender);
            return true;
        }

        if (sender instanceof Player player && args.length == 0) {
            TeamManager.unrevive(player);
            MorphManager.unmorph(player,true);
            if (TeamManager.isUndead(player)) JoinMenu.open(player);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("errors.invalid-player").replace("%player%", args[0]).send(sender);
                return true;
            }
            TeamManager.unrevive(target);
            MorphManager.unmorph(target,true);
            if (TeamManager.isUndead(target)) JoinMenu.open(target);
            Setup.updatePlayerRole(target);
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
