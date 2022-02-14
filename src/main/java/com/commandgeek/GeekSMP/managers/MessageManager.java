package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;

import org.javacord.api.entity.channel.TextChannel;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class MessageManager {
    private String message;

    public MessageManager(String key) {
        this.message = Main.messages.getString(key);
    }

    public MessageManager replace(String regex, String replacement) {
        message = message.replaceAll(regex, replacement);
        return this;
    }

    public MessageManager replace(String regex, String replacement, boolean escapeMarkdown) {
        if(escapeMarkdown) {
            String mdRegex = "[*_`~>|]";
            replacement = replacement.replaceAll(mdRegex, Matcher.quoteReplacement("\\")+"$0");
        }

        message = message.replaceAll(regex, Matcher.quoteReplacement(replacement));
        return this;
    }

    public String send(CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        return string();
    }

    public void send(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }


    public void sendDiscord(TextChannel channel) {
        channel.sendMessage(message);
    }

    public String string() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
