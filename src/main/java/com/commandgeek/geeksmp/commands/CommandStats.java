package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.apache.commons.lang.WordUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStats implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.stats") && !TeamManager.isStaff(player)) {
            new MessageManager("no-permission").send(player);
            return true;
        }

        for (String key : Main.stats.getKeys(false)) {
            String value = Main.stats.getString(key);
            if (value != null) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + WordUtils.capitalizeFully(key.replaceAll("-", " ")) + ": " + ChatColor.DARK_PURPLE + value);
            }
        }
        return true;
    }
}
