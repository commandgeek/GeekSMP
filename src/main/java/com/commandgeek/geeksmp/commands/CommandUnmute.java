package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.MuteManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnmute implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.unlink") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            MuteManager.unmute(args[0], sender);
            return true;
        }
        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
