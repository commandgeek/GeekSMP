package com.commandgeek.GeekSMP.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BypassManager {

    public static List<Player> bypass = new ArrayList<>();

    public static void toggle(Player player) {
        if (bypass.contains(player)) {
            disable(player);
        } else {
            enable(player);
        }
    }

    public static void enable(Player player) {
        bypass.add(player);
        new MessageManager("bypass-enabled").send(player);
    }

    public static void disable(Player player) {
        bypass.remove(player);
        new MessageManager("bypass-disabled").send(player);
    }

    public static boolean check(Player player) {
        return bypass.contains(player);
    }
}
