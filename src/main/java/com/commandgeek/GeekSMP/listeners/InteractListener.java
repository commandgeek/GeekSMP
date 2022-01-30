package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Animate Morphed Entity if Exists
        Entity entity = MorphManager.getEntity(player);
        if (entity != null) {
            if (!((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.BOW)) {
                new PacketManager().animateEntity(entity, 0);
            }
        }

        // If Holding Lock Tool
        if (LockManager.holdingLockTool(player) && event.getHand() == EquipmentSlot.HAND) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                LockManager.attemptLock(event.getClickedBlock(), player);
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                LockManager.attemptUnlock(event.getClickedBlock(), player);
                event.setCancelled(true);
            }
        }

        // Check if Locked
        if ((!TeamManager.isStaff(player) && !player.isOp()) || !player.isSneaking())
        if (event.getClickedBlock() != null && LockManager.isLockedForPlayer(event.getClickedBlock(), player)) {
            String owner = Main.locked.getString(LockManager.getId(event.getClickedBlock()) + ".locked");

            if (owner != null) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(owner));

                if (!LockManager.isTrustedBy(player, op)) {
                    event.setCancelled(true);
                    new MessageManager("block-locked")
                            .replace("%block%", LockManager.getName(event.getClickedBlock()))
                            .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(LockManager.getLocker(event.getClickedBlock())))).getName())
                            .send(player);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 2);
                }
            }
        }
    }

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
            return;
        }

        // Check Undead Not Near Owner
        if (TeamManager.isUndead(player) && !MorphManager.isPetNearOwner(player)) {
            event.setCancelled(true);
            return;
        }

        // Check if Locked Double Chest
        new BukkitRunnable() {
            public void run() {
                LockManager.checkLockDoubleChest(event.getBlock(), player);
            }
        }.runTaskLater(Main.instance, 1);
    }

    @EventHandler
    public void onDestroyBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        // Check Holding Lock Tool
        if (LockManager.holdingLockTool(player)) {
            event.setCancelled(true);
            return;
        }

        // Check Undead Not Near Owner
        if (TeamManager.isUndead(player) && !MorphManager.isPetNearOwner(player)) {
            event.setCancelled(true);
            return;
        }

        // Check Locked Block
        if (LockManager.isLocked(event.getBlock())) {
            if (LockManager.isLockedForPlayer(event.getBlock(), player)) {
                event.setCancelled(true);
                new MessageManager("block-locked").send(player);
                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                return;
            }
        }

        // Check if Should Remove Placer
        if (LockManager.isPlaced(event.getBlock())) {
            LockManager.unplace(event.getBlock());
        }
    }

    @EventHandler
    public void EntityDamageByEntityEvent (EntityDamageByEntityEvent event) {
        Entity en = event.getDamager();
        if (en instanceof Player){
            Player player = (Player) event.getDamager();
            if (TeamManager.isUndead(player) && !MorphManager.isPetNearOwner(player)) {
                event.setCancelled(true);

            }
        }








    }
}
