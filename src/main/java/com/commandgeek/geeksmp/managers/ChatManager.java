package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.commands.CommandSpy;

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

        if (Main.messages.contains("chat.format")) {
            String format = Main.messages.getString("chat.format");
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
                    return false;
                }

                format = format.replace("%player%", "%s");

                event.setMessage(censor(event.getMessage(), false, group));
                event.setFormat(format);
            }
        }

        try {
            String message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
            String[] words = message.split(" ");
            StringBuilder result = new StringBuilder();
            for (String word : words) {
                if (Main.lists.getStringList("banned-words").contains(word.toLowerCase().replaceAll("[\\n -@\\[-`{-¨]", ""))) {
                    for (char ignored : word.toCharArray()) {
                        result.append("\\\\*");
                    }
                    result.append(" ");
                } else {
                    result.append(word).append(" ");
                }
            }
            //noinspection ConstantConditions
            new MessageManager("discord.smp-chat.message")
                    .replace("%prefix%", ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', Main.config.getString("groups." + group + ".prefix"))))
                    .replace("%player%", player.getName(), true)
                    .replace("%message%", result.toString().trim())
                    .sendDiscord(DiscordManager.smpChatChannel);
            return true;
        } catch (IllegalArgumentException ignored) {}
        return false;
    }

    public static String censor(String message, boolean direct, String group) {
        String messageColor = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        String[] words = messageColor.split(" ");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (Main.lists.getStringList("banned-words").contains(word.toLowerCase().replaceAll("[\\n -@\\[-`{-¨]", ""))) {
                if (direct) {
                    result.append(ChatColor.MAGIC).append(word).append(Main.messages.getString("direct-message.color")).append(" ");
                } else if (group != null && Main.config.contains("groups." + group + ".chat-color")) {
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
            new MessageManager("errors.invalid-player")
                    .replace("%player%", to)
                    .send(sender);
            return;
        }
        directMessage(sender, receiver, message);
    }

    public static void directMessage(Player sender, Player receiver, String message) {
        String censoredMessage = censor(message, true, null);

        if (EntityManager.hasScoreboardTag(sender, "ignore-direct-messages")) {
            new MessageManager("direct-message.blocked-sender").send(sender);
            return;
        }

        if (EntityManager.hasScoreboardTag(receiver, "ignore-direct-messages")) {
            new MessageManager("direct-message.blocked-receiver")
                    .replace("%receiver%", receiver.getName())
                    .send(sender);
            return;
        }

        //noinspection ConstantConditions
        new MessageManager("direct-message.send")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%color%", ChatColor.translateAlternateColorCodes('&', Main.messages.getString("direct-message.color")))
                .replace("%message%", censoredMessage)
                .send(sender);
        //noinspection ConstantConditions
        new MessageManager("direct-message.receive")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%color%", ChatColor.translateAlternateColorCodes('&', Main.messages.getString("direct-message.color")))
                .replace("%message%", censoredMessage)
                .send(receiver);
        lastMessagedPlayer.put(sender, receiver);
        lastMessagedPlayer.put(receiver, sender);

        String spy = new MessageManager("direct-message.spy.message")
                .replace("%sender%", sender.getName())
                .replace("%receiver%", receiver.getName())
                .replace("%message%", censoredMessage)
                .string();
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + ChatColor.stripColor(spy));
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (CommandSpy.check(online) && online != sender && online != receiver) {
                online.sendMessage(spy);
            }
        }
    }
}
