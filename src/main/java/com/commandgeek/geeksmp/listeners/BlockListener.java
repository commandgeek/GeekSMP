package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.BypassManager;
import com.commandgeek.geeksmp.managers.LockManager;
import com.commandgeek.geeksmp.managers.MessageManager;
import com.commandgeek.geeksmp.managers.TeamManager;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;


public class BlockListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        // Check if Should Log Placer
        if (LockManager.isLockable(event.getBlock())) {
            LockManager.place(event.getBlock(), player);
        }

        // Check Holding Lock Tool
        if (LockManager.holdingLockTool(player)) {
            event.setCancelled(true);
        }

        // Check if Locked double chest or door
        new BukkitRunnable() {
            public void run() {
                LockManager.checkLockDoubleChest(event.getBlock(), player);
                LockManager.checkLockDoor(event.getBlock(), player);
            }
        }.runTaskLater(Main.instance, 1);
    }

    @EventHandler
    public void onDestroyBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        // Check Holding Lock Tool
        if (LockManager.holdingLockTool(player)) {
            event.setCancelled(true);
            return;
        }

        // Check Locked Block
        if (!((TeamManager.isStaff(player) || player.isOp()) && (player.isSneaking() || BypassManager.check(player)))) {
            if (LockManager.isLocked(block)) {
                if (LockManager.isLockedForPlayer(block, player)) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                    new MessageManager("locking.block-locked")
                            .replace("%block%", LockManager.getName(block))
                            .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(LockManager.getLocker(event.getBlock())))).getName())
                            .send(player);
                    return;
                } else {
                    LockManager.attemptUnlock(block, player);
                }
            }
        }

        // Check if Should Remove Placer
        if (LockManager.isPlaced(block)) {
            LockManager.unplace(block);
        }
    }

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
        if (event.getSource().getLocation() != null && event.getDestination().getType() == InventoryType.HOPPER) {
            Block source = event.getSource().getLocation().getBlock();
            if (event.getDestination().getLocation() != null && LockManager.isLocked(source)) {
                Block destination = event.getDestination().getLocation().getBlock();
                String sourceOwner = LockManager.getLocker(source);
                String destinationOwner = LockManager.getLocker(destination);
                if (destinationOwner == null || !destinationOwner.equals(sourceOwner)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
