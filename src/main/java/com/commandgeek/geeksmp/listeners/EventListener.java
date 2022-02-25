package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.*;

import com.destroystokyo.paper.event.inventory.PrepareResultEvent;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;


public class EventListener implements Listener {

    @EventHandler
    public void onShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity entity = MorphManager.getEntity(player);

            // Check If Morph Is Skeleton
            if (entity instanceof Skeleton) {

                // Animate Shooting
                new PacketManager().animateEntity(entity, 0);
/*                player.getInventory().remove(Material.ARROW);

                // Set Arrow Pickup Status
                if (event.getProjectile() instanceof Arrow arrow) {
                    arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                }

                // Give New Arrow
                new BukkitRunnable() {
                    public void run() {
                        player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
                    }
                }.runTaskLater(Main.instance, 20);*/
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
            new MessageManager("plugin.end-disabled").send(player);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();
        if (TeamManager.isUndead(player)) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onPrepareResult(PrepareResultEvent event) {
        // Prevent grindstoning/anviling Lock Tool
        if ((event.getInventory().getType() == InventoryType.GRINDSTONE || event.getInventory().getType() == InventoryType.ANVIL) && event.getInventory().contains(LockManager.lockTool)) {
            event.setResult(new ItemStack(Material.AIR));
        }
    }

    // Used to make boats/minecarts drop when an undead breaks them | NOT NEEDED, MADE BY MISTAKE
/*    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = event.getVehicle();

        if (event.getAttacker() instanceof Player attacker && TeamManager.isUndead(attacker) && attacker.getGameMode() == GameMode.ADVENTURE && (vehicle.getType() == EntityType.BOAT || ListManager.getEntityTypeList("minecart").contains(vehicle.getType()))) {
            if (vehicle.getType() == EntityType.BOAT) {
                Boat boat = (Boat) vehicle;
                Material material = null;
                if (boat.getWoodType() == TreeSpecies.GENERIC || boat.getWoodType() == TreeSpecies.REDWOOD) {
                    if (boat.getWoodType() == TreeSpecies.GENERIC) {
                        material = Material.OAK_BOAT;
                    }
                    if (boat.getWoodType() == TreeSpecies.REDWOOD) {
                        material = Material.SPRUCE_BOAT;
                    }
                } else {
                    material = Material.valueOf(boat.getWoodType() + "_BOAT");
                }
                if (material != null) {
                    attacker.getWorld().dropItemNaturally(vehicle.getLocation(), new ItemStack(material));
                }
            }
            if (ListManager.getEntityTypeList("minecart").contains(vehicle.getType())) {
                event.setCancelled(true);
            }
        }
    }*/
}
