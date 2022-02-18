package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.MorphManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import com.commandgeek.GeekSMP.menus.JoinMenu;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnrevive implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.unrevive")) {
            new MessageManager("no-permission").send(sender);
            return true;
        }

        if (sender instanceof Player player && args.length == 0) {
            TeamManager.unrevive(player);
            new MorphManager(player).unmorph(true);
            if (TeamManager.isUndead(player)) JoinMenu.open(player);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player").replace("%player%", args[0]).send(sender);
                return true;
            }
            TeamManager.unrevive(target);
            new MorphManager(target).unmorph(true);
            if (TeamManager.isUndead(target)) JoinMenu.open(target);
            return true;
        }

        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
