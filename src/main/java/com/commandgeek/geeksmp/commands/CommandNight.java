package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandNight implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.hasPermission("geeksmp.command.night")) {
                new MessageManager("errors.no-permission").send(player);
                return true;
            }
            if (args.length == 0 && player.getLocation().getWorld() != null) {
                player.getLocation().getWorld().setTime(13000);
                new MessageManager("plugin.time")
                        .replace("%time%", "night")
                        .replace("%world%", player.getLocation().getWorld().getName())
                        .send(player);
                return true;
            }
        }

        if (args.length == 1) {
            World world = Bukkit.getServer().getWorld(args[0]);
            if (world != null) {
                world.setTime(13000);
                new MessageManager("plugin.time")
                        .replace("%time%", "night")
                        .replace("%world%", args[0])
                        .send(sender);
            } else {
                new MessageManager("errors.invalid-world")
                        .replace("%world%", args[0])
                        .send(sender);
            }
            return true;
        }

        new MessageManager("errors.invalid-arguments").send(sender);
        return true;
    }
}
