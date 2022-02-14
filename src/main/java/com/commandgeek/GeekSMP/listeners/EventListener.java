package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Setup;
import com.commandgeek.GeekSMP.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

@SuppressWarnings({"unused"})
public class EventListener implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        if (TeamManager.isUndead(player)) {
            if (player.getBedSpawnLocation() == null)
                event.setRespawnLocation(ConfigManager.getDefaultWorldLocation(Main.config, "spawn"));
            Setup.join(player);
        }
    }

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity entity = MorphManager.getEntity(player);

            // Check If Morph Is Skeleton
            if (entity instanceof Skeleton) {

                // Set Arrow Pickup Status
                if (event.getProjectile() instanceof Arrow arrow) {
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }

                // Animate Shooting
                new PacketManager().animateEntity(entity, 0);
                player.getInventory().remove(Material.ARROW);

                // Give New Arrow
                new BukkitRunnable() {
                    public void run() {
                        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                }.runTaskLater(Main.instance, 20);
            }
        }
    }

    @EventHandler
    public void onTargetEntity(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player && TeamManager.isUndead(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL && !Main.config.getBoolean("settings.allow-end")) {
            event.setCancelled(true);
            Location loc;
            if (player.getBedSpawnLocation() != null) {
                loc =  player.getBedSpawnLocation();
            } else {
                loc = Bukkit.getWorlds().get(0).getSpawnLocation();
            }
            player.teleport(loc);
            new MessageManager("end-disabled").send(player);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (TeamManager.isUndead(player)) {
            event.setFoodLevel(20);
        }
    }
}
