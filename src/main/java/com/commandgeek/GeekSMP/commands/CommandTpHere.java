package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTpHere implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.tphere") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                new MessageManager("invalid-player")
                        .replace("%player%", args[0])
                        .send(player);
                return true;
            }
            target.teleport(player);
            return true;
        }

        new MessageManager("invalid-arguments").send(player);
        return true;
    }
}
