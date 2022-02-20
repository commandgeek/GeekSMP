package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.managers.MorphManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;


public class ItemListener implements Listener {
    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (TeamManager.isUndead(player) && !MorphManager.isMorphedPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }
}
