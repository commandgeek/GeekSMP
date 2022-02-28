package com.commandgeek.geeksmp.commands;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;
import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;


public class CommandBypass implements CommandExecutor {
    static String tag = "bypass";

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            new MessageManager("errors.console-forbidden").send(sender);
            return true;
        }
        if (!player.hasPermission("geeksmp.command.bypass") && !TeamManager.isStaff(player)) {
            new MessageManager("errors.no-permission").send(player);
            return true;
        }

        if (EntityManager.hasScoreboardTag(player, tag)) {
            player.removeScoreboardTag(tag);
            new MessageManager("locking.bypass.disabled").send(player);
        } else {
            enable(player, false);
        }
        return true;
    }

    public static void enable(Player player, boolean join) {
        if (!join) {
            player.addScoreboardTag(tag);
            new MessageManager("locking.bypass.enabled").send(player);
        }

        new BukkitRunnable() {
            public void run() {
                if (!Setup.isVanished(player)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(new MessageManager("locking.bypass.enabled").string()));
                }
                if (!check(player)) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 40);
    }

    public static boolean check(Player player) {
        return (EntityManager.hasScoreboardTag(player, tag));
    }
}
