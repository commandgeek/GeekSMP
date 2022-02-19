package com.commandgeek.GeekSMP.commands;

import com.commandgeek.GeekSMP.managers.MessageManager;

import com.commandgeek.GeekSMP.managers.MorphManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class CommandDebug implements CommandExecutor {
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
}
