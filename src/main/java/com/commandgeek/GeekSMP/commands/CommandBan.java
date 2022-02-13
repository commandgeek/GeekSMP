package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.BanManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.NumberManager;
import com.commandgeek.GeekSMP.managers.TeamManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBan implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.ban") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        if (args.length == 1) {
            BanManager.ban(args[0], null, null, sender);
            return true;
        }
        if (args.length == 2) {
            if (NumberManager.stringIsDuration(args[1])) {
                BanManager.ban(args[0], args[1], null, sender);
                return true;
            }
            if (args[1].equalsIgnoreCase("p")) {
                BanManager.ban(args[0], null, null, sender);
                return true;
            }
            new MessageManager("invalid-duration").send(sender);
            return true;
        }
        if (args.length >= 3) {
            StringBuilder reason = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }

            if (NumberManager.stringIsDuration(args[1])) {
                BanManager.ban(args[0], args[1], reason.toString().trim(), sender);
                return true;
            }
            if (args[1].equalsIgnoreCase("p")) {
                BanManager.ban(args[0], null, reason.toString().trim(), sender);
                return true;
            }
            new MessageManager("invalid-duration").send(sender);
            return true;
        }
        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
