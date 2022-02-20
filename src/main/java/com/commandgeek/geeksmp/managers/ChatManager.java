package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;


public class ChatManager {
    public static final Map<Player, Player> lastMessagedPlayer = new HashMap<>();

    public static boolean setChatMessageFromFormat(AsyncPlayerChatEvent event, String group) {
        Player player = event.getPlayer();
        String translate = Main.config.getString("groups." + group + ".translate");

        if (Main.messages.contains("chat-format")) {
            String format = Main.messages.getString("chat-format");
            if (format != null) {

                if (!Main.config.contains("groups." + group + ".translate") || (translate != null && translate.equalsIgnoreCase("false"))) {
                    event.setMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getMessage())));
                }

                if (Main.config.contains("groups." + group + ".prefix")) {
                    //noinspection ConstantConditions
                    format = format.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', Main.config.getString("groups." + group + ".prefix")));
                } else {
                    format = format.replace("%prefix%", "");
                }

                if (Main.config.contains("groups." + group + ".chat-color")) {
                    format = ChatColor.translateAlternateColorCodes('&', format.replace("%message%", Main.config.getString("groups." + group + ".chat-color") + "%s"));
                } else {
                    format = ChatColor.translateAlternateColorCodes('&', format.replace("%message%", "%s"));
                }

                format = format.replace("%player%", "%s");

                event.setMessage(censor(event.getMessage(), false, event.getPlayer(), group));
                event.setFormat(format);
            }
        }

        try {
            String message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
            String[] words = message.split(" ");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (Main.lists.getStringList("banned-words").contains(word.toLowerCase().replaceAll("[ -@\\[-`{-¨]", ""))) {
                    for (char ignored : word.toCharArray()) {
                        result.append("\\\\*");
                    }
                    result.append(" ");
                } else {
                    result.append(word).append(" ");
                }
            }
            //noinspection ConstantConditions
            new MessageManager("smp-chat-message")
                    .replace("%prefix%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.config.getString("groups." + group + ".prefix"))))
                    .replace("%player%", player.getName(), true)
                    .replace("%message%", result.toString().trim())
                    .sendDiscord(DiscordManager.smpChatChannel);
            return true;
        } catch (IllegalArgumentException ignored) {}
        return false;
    }

    public static String censor(String message, boolean direct, Player player, String group) {
        String messageColor = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        String[] words = messageColor.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (Main.lists.getStringList("banned-words").contains(word.toLowerCase().replaceAll("[ -@\\[-`{-¨]", ""))) {
                if (direct) {
                    result.append(ChatColor.MAGIC).append(word).append(Main.messages.getString("direct-message-color")).append(" ");
                } else if (Main.config.contains("groups." + group + ".chat-color")) {
                        //noinspection ConstantConditions
                        result.append(ChatColor.MAGIC).append(word).append(ChatColor.translateAlternateColorCodes('&', Main.config.getString("groups." + group + ".chat-color"))).append(" ");
                    } else {
                        result.append(ChatColor.MAGIC).append(word).append(ChatColor.RESET).append(" ");
                    }
            } else {
                result.append(word).append(" ");
            }
        }
        return result.toString().trim();
    }

    public static String joinArguments(String[] args, int start) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            result.append(args[i]).append(" ");
        }
        return result.toString().trim();
    }

    public static void directMessage(Player sender, String to, String message) {
        Player receiver = Bukkit.getPlayer(to);
        if (receiver == null) {
            new MessageManager("invalid-player")
                    .replace("%player%", to)
                    .send(sender);
            return;
        }
        directMessage(sender, receiver, message);
    }

    public static void directMessage(Player sender, Player receiver, String message) {
        message = censor(message, true, null, null);

        if (EntityManager.hasScoreboardTag(sender, "ignore-direct-messages")) {
            new MessageManager("direct-message-blocked-sender").send(sender);
            return;
        }

        if (EntityManager.hasScoreboardTag(receiver, "ignore-direct-messages")) {
            new MessageManager("direct-message-blocked-receiver")
                    .replace("%receiver%", receiver.getName())
                    .send(sender);
            return;
        }

        //noinspection ConstantConditions
        new MessageManager("direct-message-send")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%color%", ChatColor.translateAlternateColorCodes('&', Main.messages.getString("direct-message-color")))
                .replace("%message%", message)
                .send(sender);
        //noinspection ConstantConditions
        new MessageManager("direct-message-receive")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%color%", ChatColor.translateAlternateColorCodes('&', Main.messages.getString("direct-message-color")))
                .replace("%message%", message)
                .send(receiver);
        lastMessagedPlayer.put(sender, receiver);
        lastMessagedPlayer.put(receiver, sender);

        String spy = new MessageManager("spy-message")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%message%", message)
                .string();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + ChatColor.stripColor(spy));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (EntityManager.hasScoreboardTag(online, "spy-direct-messages") && online != sender && online != receiver) {
                online.sendMessage(spy);
            }
        }
    }
}
