package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.managers.LockManager;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.Objects;

@SuppressWarnings({"unused"})
public class BlockListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(LockManager::isLocked);
    }

    @EventHandler
    public void onFire(BlockBurnEvent event) {
        Block block = event.getBlock();
        if (LockManager.isLocked(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHopper(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() == InventoryType.HOPPER) {
            Block source = Objects.requireNonNull(event.getSource().getLocation()).getBlock();
            if (LockManager.isLocked(source)) {
                Block destination = Objects.requireNonNull(event.getDestination().getLocation()).getBlock();
                String sourceOwner = LockManager.getLocker(source);
                String destinationOwner = LockManager.getLocker(destination);
                if (destinationOwner == null || !destinationOwner.equals(sourceOwner)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
