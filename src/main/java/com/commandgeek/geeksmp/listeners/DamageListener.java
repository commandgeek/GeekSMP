package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.AfkManager;
import com.commandgeek.geeksmp.managers.MorphManager;
import com.commandgeek.geeksmp.managers.PacketManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;


public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (MorphManager.isMorphedEntity(event.getEntity())) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player player) {
            if (player.getWorld() == Bukkit.getWorlds().get(0) && player.getLocation().distance(Bukkit.getWorlds().get(0).getSpawnLocation()) < Main.config.getInt("settings.spawn-radius")) {
                event.setCancelled(true);
                return;
            }

            if (!(Main.config.getBoolean("settings.afk-invulnerable") && AfkManager.afk.contains(player)) && MorphManager.isMorphedPlayer(player)) {
                Entity entity = MorphManager.getEntity(player);
                if (entity != null) {
                    new PacketManager().animateEntity(entity, 1);
                    entity.getWorld().playSound(entity.getLocation(), Sound.valueOf("ENTITY_" + entity.getType() + "_HURT"), 1, 1);
                }
            }
        }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (damager instanceof Player || damager instanceof Arrow || damager instanceof Trident || damager instanceof IronGolem || damager instanceof Wolf) {
            Player entity = MorphManager.getPlayer(victim);

            if (damager instanceof Arrow arrow) {
                arrow.remove();
            }

            if (entity != null && damager.getUniqueId() != entity.getUniqueId()) {
                entity.damage(event.getDamage(), damager);
                event.setCancelled(true);
            }

            // AFK invulnerability
            if (Main.config.getBoolean("settings.afk-invulnerable")) {
                if (entity != null) {
                    if (AfkManager.afk.contains(entity)) {
                        event.setCancelled(true);
                    }
                } else if (victim instanceof Player player && AfkManager.afk.contains(player)) {
                    event.setCancelled(true);
                }
            }
        }

        // Prevent undeads from destroying item frames or armor stands
        if ((victim instanceof ItemFrame || victim instanceof ArmorStand) && damager instanceof Player player) {
            if (TeamManager.isUndead(player)) {
                if (MorphManager.pets()) {
                    if (!MorphManager.isPetNearOwner(player)) {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
