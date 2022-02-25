package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.DiscordManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CommandBroadcast implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player && !player.hasPermission("geeksmp.command.broadcast") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (args.length >= 1) {
            StringBuilder message = new StringBuilder();
            String minecraft = "broadcast.minecraft";
            String discord = "broadcast.discord";
            String fail = "broadcast.not-sent";

            for (String item : args) {
                message.append(" ");
                message.append(item);
            }

            if (Main.messages.getString(minecraft) != null) {
                //noinspection ConstantConditions
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', Main.messages.getString(minecraft)
                        .replace("%message%", message.toString())));
            } else if (Main.messages.getString(fail) != null) {
                new MessageManager(fail)
                        .replace("%platform%", "Minecraft")
                        .replace("%path%", minecraft)
                        .send(sender);
            }

            if (Main.messages.getString(discord) != null) {
                new MessageManager(discord)
                        .replace("%message%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.toString())))
                        .sendDiscord(DiscordManager.smpChatChannel);
            } else if (Main.messages.getString(fail) != null) {
                new MessageManager(fail)
                        .replace("%platform%", "Discord")
                        .replace("%path%", discord)
                        .send(sender);
            }

        } else {
            new MessageManager("errors.invalid-arguments").send(sender);
        }
        return true;
    }
}
