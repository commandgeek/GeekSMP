package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;

public class ChatManager {

    public static Map<Player, Player> lastMessagedPlayer = new HashMap<>();

    public static boolean setChatMessageFromFormat(AsyncPlayerChatEvent event, String name) {
        Player player = event.getPlayer();
        if (Main.config.contains("groups." + name + ".chat-format")) {

            String format = Main.config.getString("groups." + name + ".chat-format");
            if (format != null) {
                if (!Main.config.contains("groups." + name + ".translate")) {
                    String translate = Main.config.getString("groups." + name + ".translate");
                    if (translate != null && translate.equalsIgnoreCase("false")) {
                        event.setMessage(ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getMessage())));
                    }
                }
                format = format.replaceAll("%player%", "%s");
                format = ChatColor.translateAlternateColorCodes('&', format.replaceAll("%message%", "%s"));

                event.setMessage(ChatManager.censor(event.getMessage()));

                event.setFormat(format);

                try {
                    new MessageManager("smp-chat-message")
                            .replace("%player%", player.getName())
                            .replace("%message%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getMessage())))
                            .escapeMarkdown()
                            .sendDiscord(DiscordManager.smpChatChannel);
                    return true;
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return false;
    }

    public static String censor(String message) {
        String[] words = message.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (Main.bannedWords.contains(word.toLowerCase())) {
                for (char ignored : word.toCharArray()) {
                    result.append("*");
                }
                result.append(" ");
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
        message = censor(message);

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

        new MessageManager("direct-message-send")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%message%", message)
                .send(sender);
        new MessageManager("direct-message-receive")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
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
