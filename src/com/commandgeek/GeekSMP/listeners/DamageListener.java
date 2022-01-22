package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.LockManager;
import com.commandgeek.GeekSMP.managers.MorphManager;
import com.commandgeek.GeekSMP.managers.PacketManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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
        if (event.getDamager() instanceof Player || event.getDamager() instanceof Arrow) {
            if (event.getDamager() instanceof Arrow arrow) {
                arrow.remove();
            }
            try {
                LivingEntity entity = (LivingEntity) event.getEntity();
                Player victim = MorphManager.getPlayer(entity);
                if (victim != null) {
                    victim.damage(event.getDamage(), event.getDamager());
                    event.setCancelled(true);
                }
            } catch (ClassCastException ignored) {}
        }
    }
}
