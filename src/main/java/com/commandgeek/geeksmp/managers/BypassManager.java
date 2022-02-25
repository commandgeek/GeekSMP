package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;


public class BypassManager {
    static final List<String> bypass = Main.bypass.getStringList("bypass");

    public static void toggle(Player player) {
        if (check(player)) {
            disable(player);
        } else {
            enable(player, true);
        }
    }

    public static void enable(Player player, boolean persistent) {
        if (persistent) {
            bypass.add(player.getUniqueId().toString());
            Main.bypass.set("bypass", bypass);
            ConfigManager.saveData("bypass.yml", Main.bypass);
            new MessageManager("locking.bypass.enabled").send(player);
        }

        new BukkitRunnable() {
            public void run() {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(new MessageManager("locking.bypass.enabled").string()));
                if (!check(player)) {
                    cancel();
                }
            }
        }.runTaskTimer(Main.instance, 0, 40);
    }

    public static void disable(Player player) {
        bypass.remove(player.getUniqueId().toString());
        Main.bypass.set("bypass", bypass);
        ConfigManager.saveData("bypass.yml", Main.bypass);
        new MessageManager("locking.bypass.disabled").send(player);
    }

    public static boolean check(Player player) {
        return (Main.bypass.getStringList("bypass").contains(player.getUniqueId().toString()));
    }
}
