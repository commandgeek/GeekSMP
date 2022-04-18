package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class BanManager {

    public static void ban(String name, String duration, String reason, CommandSender sender) {
        OfflinePlayer op = EntityManager.getOfflinePlayer(name);
        if (op == null) {
            new MessageManager("errors.invalid-player").replace("%player%", name).send(sender);
            return;
        }
        if (op.isOnline()) {
            Player player = (Player) op;
            Setup.updatePlayerRole(player);
            player.kickPlayer("You are banned!");
        }
        UUID id = op.getUniqueId();
        long time = new Timestamp(System.currentTimeMillis()).getTime();
        Main.banned.set(id + ".from", time);
        Main.banned.set(id + ".reason", Objects.requireNonNullElse(reason, "Banned by an operator"));
        if (duration == null) {
            Main.banned.set(id + ".until", -1);
            ConfigManager.saveData("banned.yml", Main.banned);
            new MessageManager("punishing.banning.ban.permanent").replace("%player%", name).send(sender);
            return;
        }
        long dur = Integer.parseInt(duration.replaceAll("(^\\d+)(.*)", "$1"));
        if (duration.endsWith("m")) {
            Main.banned.set(id + ".until", dur * 60 * 1000 + time);
            ConfigManager.saveData("banned.yml", Main.banned);
            if (dur == 1)
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " minute").send(sender);
            else
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " minutes").send(sender);
            return;
        }
        if (duration.endsWith("h")) {
            Main.banned.set(id + ".until", dur * 60 * 60 * 1000 + time);
            ConfigManager.saveData("banned.yml", Main.banned);
            if (dur == 1)
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " hour").send(sender);
            else
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " hours").send(sender);
            return;
        }
        if (duration.endsWith("d")) {
            Main.banned.set(id + ".until", dur * 60 * 60 * 24 * 1000 + time);
            ConfigManager.saveData("banned.yml", Main.banned);
            if (dur == 1)
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " day").send(sender);
            else
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " days").send(sender);
            return;
        }
        if (duration.endsWith("w")) {
            Main.banned.set(id + ".until", dur * 60 * 60 * 24 * 7 * 1000 + time);
            ConfigManager.saveData("banned.yml", Main.banned);
            if (dur == 1)
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " week").send(sender);
            else
                new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " weeks").send(sender);
            return;
        }
        Main.banned.set(id + ".until", dur * 1000 + time);
        ConfigManager.saveData("banned.yml", Main.banned);
        if (dur == 1)
            new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " second").send(sender);
        else
            new MessageManager("punishing.banning.ban.temporary").replace("%player%", name).replace("%duration%", dur + " seconds").send(sender);
    }

    public static boolean isBanned(UUID uuid) {
        return checkBanned(uuid.toString()) != 0;
    }

    public static long checkBanned(String uuid) {
        if (Main.banned.contains(uuid)) {
            if (!Main.banned.contains(uuid + ".until")) {
                Main.banned.set(uuid, null);
                ConfigManager.saveData("banned.yml", Main.banned);
                return 0;
            }
            if (Main.banned.getLong(uuid + ".until") == -1) {
                return -1;
            }
            long time = new Timestamp(System.currentTimeMillis()).getTime();
            if (time >= Main.banned.getLong(uuid + ".until")) {
                Main.banned.set(uuid, null);
                ConfigManager.saveData("banned.yml", Main.banned);
                return 0;
            }
            return Main.banned.getLong(uuid + ".until") - time;
        }
        return 0;
    }

    public static void unban(String name, CommandSender sender) {
        OfflinePlayer op = EntityManager.getOfflinePlayer(name);
        if (op != null) {
            UUID id = op.getUniqueId();
            if (Main.banned.contains(String.valueOf(id))) {
                Main.banned.set(String.valueOf(id), null);
                ConfigManager.saveData("banned.yml", Main.banned);
                new MessageManager("punishing.banning.unban.success").replace("%player%", name).send(sender);
                return;
            }
        }
        new MessageManager("punishing.banning.unban.fail").replace("%player%", name).send(sender);
    }

    public static String getReason(UUID uuid) {
        if (Main.banned.contains(uuid.toString())) {
            return Main.banned.getString(uuid + ".reason");
        }
        return null;
    }
}
