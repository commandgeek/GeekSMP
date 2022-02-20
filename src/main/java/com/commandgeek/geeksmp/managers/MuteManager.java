package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.Setup;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;

public class MuteManager {

    public static void mute(String name, String duration, String reason, CommandSender sender) {
        OfflinePlayer op = EntityManager.getOfflinePlayer(name);
        if (op == null) {
            new MessageManager("invalid-player").replace("%player%", name).send(sender);
            return;
        }

        UUID id = op.getUniqueId();
        long time = new Timestamp(System.currentTimeMillis()).getTime();
        Main.muted.set(id + ".from", time);
        Main.muted.set(id + ".reason", Objects.requireNonNullElse(reason, "Muted by an operator"));
        if (duration == null) {
            Main.muted.set(id + ".until", -1);
            ConfigManager.saveData("muted.yml", Main.muted);
            Setup.updateMemberRoles(op.getUniqueId());
            new MessageManager("mute-permanent").replace("%player%", name).send(sender);
            return;
        }
        long dur = Integer.parseInt(duration.replaceAll("(^[0-9]+)(.*)", "$1"));
        if (duration.endsWith("m")) {
            Main.muted.set(id + ".until", dur * 60 * 1000 + time);
            ConfigManager.saveData("muted.yml", Main.muted);
            Setup.updateMemberRoles(op.getUniqueId());
            if (dur == 1)
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " minute").send(sender);
            else
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " minutes").send(sender);
            return;
        }
        if (duration.endsWith("h")) {
            Main.muted.set(id + ".until", dur * 60 * 60 * 1000 + time);
            ConfigManager.saveData("muted.yml", Main.muted);
            Setup.updateMemberRoles(op.getUniqueId());
            if (dur == 1)
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " hour").send(sender);
            else
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " hours").send(sender);
            return;
        }
        if (duration.endsWith("d")) {
            Main.muted.set(id + ".until", dur * 60 * 60 * 24 * 1000 + time);
            ConfigManager.saveData("muted.yml", Main.muted);
            Setup.updateMemberRoles(op.getUniqueId());
            if (dur == 1)
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " day").send(sender);
            else
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " days").send(sender);
            return;
        }
        if (duration.endsWith("w")) {
            Main.muted.set(id + ".until", dur * 60 * 60 * 24 * 7 * 1000 + time);
            ConfigManager.saveData("muted.yml", Main.muted);
            Setup.updateMemberRoles(op.getUniqueId());
            if (dur == 1)
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " week").send(sender);
            else
                new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " weeks").send(sender);
            return;
        }
        Main.muted.set(id + ".until", dur * 1000 + time);
        ConfigManager.saveData("muted.yml", Main.muted);
        Setup.updateMemberRoles(op.getUniqueId());
        if (dur == 1)
            new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " second").send(sender);
        else
            new MessageManager("mute-temporary").replace("%player%", name).replace("%duration%", dur + " seconds").send(sender);
    }

    public static boolean isMuted(UUID uuid) {
        return checkMuted(uuid.toString()) != 0;
    }

    public static long checkMuted(String uuid) {
        if (Main.muted.contains(uuid)) {
            if (!Main.muted.contains(uuid + ".until")) {
                Main.muted.set(uuid, null);
                ConfigManager.saveData("muted.yml", Main.muted);
                Setup.updateMemberRoles(UUID.fromString(uuid));
                return 0;
            }
            if (Main.muted.getLong(uuid + ".until") == -1) {
                return -1;
            }
            long time = new Timestamp(System.currentTimeMillis()).getTime();
            if (time >= Main.muted.getLong(uuid + ".until")) {
                Main.muted.set(uuid, null);
                ConfigManager.saveData("muted.yml", Main.muted);
                Setup.updateMemberRoles(UUID.fromString(uuid));
                return 0;
            }
            return Main.muted.getLong(uuid + ".until") - time;
        }
        return 0;
    }

    public static void unmute(String name, CommandSender sender) {
        OfflinePlayer op = EntityManager.getOfflinePlayer(name);
        if (op != null) {
            UUID id = op.getUniqueId();
            if (Main.muted.contains(String.valueOf(id))) {
                Main.muted.set(String.valueOf(id), null);
                ConfigManager.saveData("muted.yml", Main.muted);
                Setup.updateMemberRoles(op.getUniqueId());
                new MessageManager("unmute-success").replace("%player%", name).send(sender);
                return;
            }
        }
        new MessageManager("unmute-fail").replace("%player%", name).send(sender);
    }

    public static String getReason(UUID uuid) {
        if (Main.muted.contains(uuid.toString())) {
            return Main.muted.getString(uuid + ".reason");
        }
        return null;
    }
}
