package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class MoveListener implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        AfkManager.moved(player);
        if (AfkManager.check(player)) {
            AfkManager.disable(player);
        }

        // Don't let unmorphed undeads move
        if (TeamManager.isUndead(player) && !MorphManager.isMorphedPersistent(player)) {
            event.setCancelled(true);
        }

        Entity entity = MorphManager.getEntity(player);
        if (entity != null) {
            entity.teleport(player.getLocation());
            new PacketManager(player).hideEntity(entity);
        }

        if (TeamManager.isUndead(player)) {
            if (MorphManager.pets()) {
                if (MorphManager.isPetNearOwner(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                } else {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }
            if (player.isGliding()) {
                player.setGliding(false);
            }
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!player.isSneaking() && player.getInventory().getItemInMainHand().isSimilar(LockManager.lockTool)) {
            new BukkitRunnable() {
                public void run() {
                    LockManager.showLockedLocations(player);
                    if (!player.isSneaking()) {
                        cancel();
                    }
                }
            }.runTaskTimer(Main.instance, 1, 5);
        }
    }

    @EventHandler
    public void onGlide(EntityToggleGlideEvent event) {
        if (event.getEntity() instanceof Player player && TeamManager.isUndead(player)) {
            event.setCancelled(true);
        }
    }
}
