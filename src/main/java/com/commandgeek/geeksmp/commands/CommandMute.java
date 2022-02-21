package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMute implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.mute") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            MuteManager.mute(args[0], null, null, sender);
            return true;
        }
        if (args.length == 2) {
            if (NumberManager.stringIsDuration(args[1])) {
                MuteManager.mute(args[0], args[1], null, sender);
                return true;
            }
            if (args[1].equalsIgnoreCase("p")) {
                MuteManager.mute(args[0], null, null, sender);
                return true;
            }
            new MessageManager("errors.invalid-duration").send(sender);
            return true;
        }
        if (args.length >= 3) {
            StringBuilder reason = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }

            if (NumberManager.stringIsDuration(args[1])) {
                MuteManager.mute(args[0], args[1], reason.toString().trim(), sender);
                return true;
            }
            if (args[1].equalsIgnoreCase("p")) {
                MuteManager.mute(args[0], null, reason.toString().trim(), sender);
                return true;
            }
            new MessageManager("errors.invalid-duration").send(sender);
            return true;
        }
        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
