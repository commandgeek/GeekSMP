package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInspect implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }

        if (!player.hasPermission("geeksmp.command.inspect") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        boolean isOp = player.isOp();
        if (!isOp) player.setOp(true);

        if (args.length == 0) {
            player.performCommand("coreprotect:co inspect");
        }
        if (args.length == 1) {
            player.performCommand("coreprotect:co lookup " + args[0]);
        }
        if (!isOp) player.setOp(false);
        return true;
    }
}
