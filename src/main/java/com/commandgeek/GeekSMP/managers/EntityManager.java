package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Set;

public class EntityManager {

    public static void hidePlayer(Player entity, Player player) {
        player.hidePlayer(Main.instance, entity);
        new PacketManager(player).addPlayer(entity);
    }

    public static void hidePlayerForAll(Player player, Player... excludes) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            boolean hide = true;
            for (Player exclude : excludes) {
                if (online == exclude || online == player) {
                    hide = false;
                    break;
                }
            }
            if (hide) {
                hidePlayer(player, online);
            }
        }
    }

    public static void showPlayer(Player entity, Player player) {
        player.showPlayer(Main.instance, entity);
    }

    public static void showPlayerForAll(Player player, Player... excludes) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            boolean show = true;
            for (Player exclude : excludes) {
                if (online == exclude) {
                    show = false;
                    break;
                }
            }
            if (show) {
                showPlayer(player, online);
            }
        }
    }

    public static void hideEntity(Entity entity, Player player) {
        if (entity instanceof Player playerEntity && playerEntity == player) return;
        new PacketManager(player).hideEntity(entity);
    }

    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer[] ops = Bukkit.getOfflinePlayers();
        for (OfflinePlayer op : ops) {
            if (op != null && op.getName() != null && op.getName().equalsIgnoreCase(name)) {
                return op;
            }
        }
        return null;
    }

    public static void checkHiddenPlayer(Player check, Player player) {
        Set<String> keys = Main.morphs.getKeys(false);
        if (keys.contains(check.getUniqueId().toString())) {
            new BukkitRunnable() {
                public void run() {
                    hidePlayer(check, player);
                }
            }.runTaskLater(Main.instance, 0);
        }
    }

    public static void checkHiddenPlayers(Player player) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            checkHiddenPlayer(online, player);
        }
    }

    public static boolean hasScoreboardTag(Player player, String check) {
        for (String tag : player.getScoreboardTags()) {
            if (tag.equals(check)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPlayerNear(Location loc, double radius) {
        World world = loc.getWorld();
        if (world != null) {
            Collection<Entity> entities = world.getNearbyEntities(loc, radius, radius, radius, (entity) -> entity.getType() == EntityType.PLAYER);
            for (Entity entity : entities) {
                if (!TeamManager.isUndead((Player) entity)) {
                    return true;
                }
            }
        }
        return false;
    }
}
