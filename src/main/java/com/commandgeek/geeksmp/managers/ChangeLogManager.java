package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.ChangeLog;

import org.javacord.api.entity.message.embed.EmbedBuilder;

import org.bukkit.command.CommandSender;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ChangeLogManager {

    public static final Map<CommandSender, ChangeLog> changeLogs = new HashMap<>();

    public static void create(CommandSender sender) {
        changeLogs.put(sender, new ChangeLog());
    }

    public static void addItem(CommandSender sender, String string) {
        ChangeLog log;
        if (changeLogs.containsKey(sender)) {
            log = changeLogs.get(sender);
        } else {
            log = new ChangeLog();
        }
        log.addItem(string);
        changeLogs.put(sender, log);
    }

    public static void removeLastItem(CommandSender sender) {
        if (changeLogs.containsKey(sender) && changeLogs.get(sender).items.size() > 0) {
            ChangeLog log = changeLogs.get(sender);
            log.removeLastItem();
            changeLogs.put(sender, log);
        }
    }

    public static void send(CommandSender sender) {
        if (changeLogs.containsKey(sender)) {
            ChangeLog log = changeLogs.get(sender);

            new MessageManager("change-log-create-header")
                    .replace("%version%", log.title)
                    .send(sender);
            for (String item : log.items) {
                new MessageManager("change-log-create-item")
                        .replace("%item%", item)
                        .send(sender);
            }
        }
    }

    public static void publish(CommandSender sender) {

        if (changeLogs.containsKey(sender)) {
            ChangeLog log = changeLogs.get(sender);

            EmbedBuilder embed = new EmbedBuilder();
            String title = new MessageManager("change-log-publish-header")
                    .replace("%version%", log.title)
                    .string();
            embed.setTitle(title);
            embed.setFooter(new MessageManager("change-log-publish-footer").string());
            embed.setColor(Color.decode(new MessageManager("change-log-publish-color").string()));

            StringBuilder items = new StringBuilder();
            for (String item : log.items) {
                String format = new MessageManager("change-log-publish-item")
                        .replace("%item%", item)
                        .string();
                items.append(format).append("\n");
            }
            embed.setDescription(items.toString().trim());
            DiscordManager.changeLogChannel.sendMessage(embed);
            new MessageManager("change-log-publish-success").send(sender);
        }
    }
}
