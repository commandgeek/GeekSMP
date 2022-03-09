package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AfkManager {
    public static final List<Player> afk = new ArrayList<>();
    public static final Map<Player, Long> lastMoved = new HashMap<>();

    public static void toggle(Player player) {
        if (afk.contains(player)) {
            disable(player);
        } else {
            enable(player);
        }
    }

    public static void enable(Player player) {
        afk.add(player);
        Team team = TeamManager.getPlayerTeam(player);
        if (team != null) {
            String prefix = team.getPrefix() + team.getColor();
            String suffix = ChatColor.translateAlternateColorCodes('&', " &8[&7AFK&8]");
            if (Main.messages.getString("afk.suffix") != null) {
                //noinspection ConstantConditions
                suffix = ChatColor.translateAlternateColorCodes('&', Main.messages.getString("afk.suffix"));
            }

            player.setPlayerListName(prefix + player.getName() + suffix);
            Bukkit.broadcastMessage(new MessageManager("afk.enabled").replace("%player%", player.getName()).string());
        }
    }

    public static void disable(Player player) {
        afk.remove(player);
        player.setPlayerListName(player.getName());
        Bukkit.broadcastMessage(new MessageManager("afk.disabled").replace("%player%", player.getName()).string());
    }

    public static boolean check(Player player) {
        return afk.contains(player);
    }

    public static void update(Player player) {
        if (!check(player) && lastMoved.containsKey(player)) {
            long difference = Math.abs(lastMoved.get(player) - new Timestamp(System.currentTimeMillis()).getTime());
            if (difference > 1000 * 60 * 5) {
                enable(player);
            }
        }
    }

    public static void moved(Player player) {
        lastMoved.put(player, new Timestamp(System.currentTimeMillis()).getTime());
    }
}
