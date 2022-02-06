package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.MorphManager;
import com.commandgeek.GeekSMP.managers.PacketManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

@SuppressWarnings({"unused"})
public class DamageListener implements Listener {

    public static int spawnRadius = Main.config.getInt("settings.spawn-radius");

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (MorphManager.isMorphedEntity(event.getEntity())) {
            event.setCancelled(true);
            return;
        }

        if (event.getEntity() instanceof Player player) {
            if (player.getWorld() == Bukkit.getWorlds().get(0) && player.getLocation().distance(Bukkit.getWorlds().get(0).getSpawnLocation()) < spawnRadius) {
                event.setCancelled(true);
                return;
            }
            if (MorphManager.isMorphedPlayer(player)) {
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

        if (damager instanceof Player || damager instanceof Arrow || damager instanceof Trident || damager instanceof IronGolem) {
            if (damager instanceof Arrow arrow) {
                arrow.remove();
            }

            Entity entity = event.getEntity();
            Player victim = MorphManager.getPlayer(entity);
            if (victim != null) {
                victim.damage(event.getDamage(), damager);
                event.setCancelled(true);
            }
        }

        if (event.getEntity() instanceof ItemFrame entity && event.getDamager() instanceof Player player) {
            if(TeamManager.isUndead(player)) event.setCancelled(true);
        }
    }
}
