package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings({"unused"})
public class ItemListener implements Listener {

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {

            if (TeamManager.isUndead(player) && !MorphManager.isMorphedPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (TeamManager.isUndead(player) && event.getItem().getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
        }
    }
}
