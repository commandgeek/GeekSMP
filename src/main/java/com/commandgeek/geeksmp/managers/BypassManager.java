package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BypassManager {

    public static final List<Player> bypass = new ArrayList<>();

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
        new BukkitRunnable() {
            public void run() {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(new MessageManager("bypass-enabled").string()));
                if (!bypass.contains(player)) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 40);
    }

    public static void disable(Player player) {
        bypass.remove(player);
        new MessageManager("bypass-disabled").send(player);
    }
}
