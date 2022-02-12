package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandIP implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.ip") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
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

            String IP = target.getAddress().getHostString();
            sender.sendMessage("The IP of " + target.getName() + " is " + ChatColor.BOLD + IP);
            return true;
        } else if(sender instanceof Player player && args.length == 0) {
            String IP = player.getAddress().getHostString();
            sender.sendMessage("Your IP is " + ChatColor.BOLD + IP);
            return true;
        }

        new MessageManager("invalid-arguments").send(sender);
        return true;
    }
}
