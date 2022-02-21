package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.managers.MessageManager;

import com.commandgeek.geeksmp.managers.MorphManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class CommandDebug implements TabExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player player) {

            if (!player.hasPermission("geeksmp.command.debug")) {
                new MessageManager("no-permission").send(player);
                return true;
            }

            if (args.length == 1) {
                if (args[0].equals("item0")) {
                    player.getInventory().setItem(0, new ItemStack(Material.DIAMOND_SWORD));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dSet item slot &50 &dto &5DIAMOND_SWORD &dfor &5" + player.getName()));
                    return true;
                }

                if (args[0].equals("item27")) {
                    player.getInventory().setItem(27, new ItemStack(Material.DIAMOND_SWORD));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dSet item slot &527 &dto &5DIAMOND_SWORD &dfor &5" + player.getName()));
                    return true;
                }

                if (args[0].equals("skeletonbow")) {
                    player.getInventory().addItem(MorphManager.skeletonBow());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dGave you &5Skeleton Bow &dto &5" + player.getName()));
                    return true;
                }

                if (args[0].equals("skeletonarrow")) {
                    player.getInventory().addItem(MorphManager.skeletonArrow());
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&dGave &5Skeleton Arrow &dto &5" + player.getName()));
                    return true;
                }
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            suggestions.add("item0");
            suggestions.add("item27");
            suggestions.add("skeletonbow");
            suggestions.add("skeletonarrow");
        }

        for (String suggestion : suggestions) {
            if (suggestion.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                results.add(suggestion);
            }
        }
        return results;
    }
}
