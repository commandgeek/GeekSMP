package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandRevive implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.revive")) {
            new MessageManager("no-permission").send(sender);
            return true;
        }

        if (sender instanceof Player player && args.length == 0) {
            TeamManager.revive(player);
            MorphManager.unmorph(player,true);
            EntityManager.showPlayerForAll(player);
            Setup.updatePlayerRole(player);
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player")
                        .replace("%player%", args[0])
                        .send(sender);
                return true;
            }
            TeamManager.revive(target);
            MorphManager.unmorph(target,true);
            EntityManager.showPlayerForAll(target);
            Setup.updatePlayerRole(target);
            return true;
        }

        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
