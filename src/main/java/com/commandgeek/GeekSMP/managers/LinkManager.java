package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Setup;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkManager {

    public static Map<UUID, String> linkingCodes = new HashMap<>();

    private static final Server server = DiscordManager.server;

    public static UUID getPlayerUUID(String id) {
        String uuid = Main.linked.getString(id);
        if (uuid != null) return UUID.fromString(uuid);
        return null;
    }

    public static String getDiscordID(UUID uuid) {
        for (String key : Main.linked.getKeys(false)) {
            String value = Main.linked.getString(key);
            if (value != null && value.equalsIgnoreCase(uuid.toString())) {
                return key;
            }
        }
        return null;
    }

    public static void link(String id, UUID uuid) {
        Main.linked.set(id, uuid.toString());
        ConfigManager.saveData("linked.yml", Main.linked);
        removeCode(uuid);
        linkEvent(id, uuid);
    }

    public static void linkEvent(String id, UUID uuid) {
        User user = DiscordManager.getUserFromId(id);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Setup.updatePlayerRole(player);
            if (user != null) {
                new MessageManager("link-success")
                        .replace("%user%", user.getDiscriminatedName())
                        .send(player);
            }
        }
        if (user != null) {
            user.addRole(DiscordManager.linkedRole);
            user.updateNickname(server, Bukkit.getOfflinePlayer(uuid).getName());
            if (!TeamManager.isUndead(player))
                EntityManager.showPlayerForAll(player);
        }
    }

    public static void unlink(String id) {
        String value = Main.linked.getString(id);
        if (value != null) unlinkEvent(id, UUID.fromString(value));
        Main.linked.set(id, null);
        ConfigManager.saveData("linked.yml", Main.linked);
    }

    public static void unlink(UUID uuid) {
        for (String key : Main.linked.getKeys(false)) {
            String value = Main.linked.getString(key);
            if (value != null && value.equalsIgnoreCase(uuid.toString())) {
                Main.linked.set(key, null);
                ConfigManager.saveData("linked.yml", Main.linked);
                unlinkEvent(key, uuid);
                break;
            }
        }
    }

    public static void unlinkEvent(String id, UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            Setup.updatePlayerRole(player);
        }

        User user = DiscordManager.getUserFromId(id);
        if (user != null) {
            user.removeRole(DiscordManager.linkedRole);
            user.resetNickname(server);
        }
    }

    public static String generateCode(UUID uuid) {
        String code = String.valueOf(NumberManager.randomInt()) +
                NumberManager.randomInt() +
                NumberManager.randomInt() +
                NumberManager.randomInt();
        linkingCodes.put(uuid, code);
        return code;
    }

    public static void removeCode(UUID uuid) {
        linkingCodes.remove(uuid);
    }

    public static UUID getUUIDFromCode(String code) {
        for (Map.Entry<UUID, String> entry : linkingCodes.entrySet()) {
            if (entry.getValue().equals(code)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
