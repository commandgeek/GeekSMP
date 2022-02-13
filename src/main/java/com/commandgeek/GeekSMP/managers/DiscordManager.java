package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class DiscordManager {

    public static Server server = getMainServer();

    public static TextChannel smpChatChannel;
    public static TextChannel changeLogChannel;
    public static List<TextChannel> linkChannels;

    public static Role mutedRole;
    public static Role linkedRole;

    public static Server getMainServer() {
        assert Main.discordAPI != null;
        for (Server server : Main.discordAPI.getServers()) {
            return server;
        }
        Bukkit.getLogger().warning("Failed to get Main Discord Server!");
        return null;
    }

    public static User getUserFromId(String id) {
        try {
            return Main.discordAPI.getUserById(id).get();
        } catch (InterruptedException | ExecutionException ignored) {
            return null;
        }
    }


    public static User getUserFromPlayer(Player player) {
        return getUserFromUuid(player.getUniqueId());
    }

    public static User getUserFromUuid(UUID uuid) {
        String id = LinkManager.getDiscordID(uuid);
        if (id == null || id.isEmpty()) return null;
        return getUserFromId(id);
    }

    public static OfflinePlayer getPlayerFromUser(User user) {
        UUID uuid = LinkManager.getPlayerUUID(user.getIdAsString());
        if (uuid == null) return null;
        return Bukkit.getOfflinePlayer(uuid);
    }

    public static boolean userHasRole(User user, String id) {
        for (Role role : user.getRoles(server)) {
            if (role.getIdAsString().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public static void privateMessage(User user, String message) {
        try {
            user.openPrivateChannel().get().sendMessage(message);
        } catch (InterruptedException | ExecutionException ignored) {
            Bukkit.getLogger().info("Unable to send direct message to " + user.getDiscriminatedName());
        }
    }

    public static TextChannel channelFromConfig(FileConfiguration config, String key) {
        if (config.contains(key)) {
            String id = config.getString(key);
            if (id != null) {
                if (Main.discordAPI.getTextChannelById(id).isPresent()) {
                    return Main.discordAPI.getTextChannelById(id).get();
                }
            }
        }
        return null;
    }

    public static List<TextChannel> channelsFromConfig(FileConfiguration config, String key) {
        List<TextChannel> channels = new ArrayList<>();
        if (config.isList(key)) {
            for (String id : config.getStringList(key)) {
                if (Main.discordAPI.getTextChannelById(id).isPresent()) {
                    channels.add(Main.discordAPI.getTextChannelById(id).get());
                }
            }
        }
        return channels;
    }

    public static Role roleFromConfig(FileConfiguration config, String key) {
        if (config.contains(key)) {
            String id = config.getString(key);
            if (id != null) {
                if (Main.discordAPI.getRoleById(id).isPresent()) {
                    return Main.discordAPI.getRoleById(id).get();
                }
            }
        }
        return null;
    }

    public static User getUserFromMessage(Message message) {
        return message.getAuthor().asUser().isPresent() ? message.getAuthor().asUser().get() : null;
    }

    public static void sendInfo(String info, TextChannel channel) {
        if (Main.info.contains(info + ".image")) {
            EmbedBuilder image = new EmbedBuilder();
            image.setImage(Main.info.getString(info + ".image"));
            if (Main.info.contains(info + ".color")) {
                String color = Main.info.getString(info + ".color");
                if (color != null) {
                    image.setColor(Color.decode(color));
                }
            }
            channel.sendMessage(image);
        }
        ConfigurationSection section = Main.info.getConfigurationSection(info);
        if (section != null) {
            for (String key : section.getKeys(false)) {
                if (Main.info.isList(info + "." + key)) {
                    EmbedBuilder embed = new EmbedBuilder();
                    String title = key.replaceAll("-", " ").toUpperCase();
                    embed.setTitle("**__" + title + "__**");
                    StringBuilder value = new StringBuilder();
                    for (String rule : Main.info.getStringList(info + "." + key)) {
                        value.append(rule).append("\n");
                    }
                    embed.setDescription(value.toString());
                    if (Main.info.contains(info + ".color")) {
                        String color = Main.info.getString(info + ".color");
                        if (color != null) {
                            embed.setColor(Color.decode(color));
                        }
                    }
                    channel.sendMessage(embed);
                }
            }
        }
    }
}
