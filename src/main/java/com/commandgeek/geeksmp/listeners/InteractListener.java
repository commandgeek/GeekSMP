package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.commands.CommandBypass;
import com.commandgeek.geeksmp.managers.*;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;
import java.util.UUID;


public class InteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Animate Morphed Entity if Exists
        boolean action = (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK);
        if (MorphManager.getEntity(player) != null && !(action && event.getItem() != null && event.getMaterial() == Material.BOW)) {
            new PacketManager().animateEntity(MorphManager.getEntity(player), 0);
        }

        // If Holding Lock Tool
        if (LockManager.holdingLockTool(player) && event.getHand() == EquipmentSlot.HAND && block != null) {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                LockManager.attemptLock(block, player);
                event.setCancelled(true);
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                LockManager.attemptUnlock(block, player);
                event.setCancelled(true);
            }
        }

        // Check if locked
        if (block != null && LockManager.isLockedForPlayer(block, player)) {
            String owner = Main.locked.getString(LockManager.getId(block) + ".locked");
            if (owner != null && !LockManager.isTrustedBy(player, Bukkit.getOfflinePlayer(UUID.fromString(owner)))) {
                boolean bypass = (TeamManager.isStaff(player) || player.isOp()) && (player.isSneaking() || CommandBypass.check(player));
                if (!bypass) {
                    event.setCancelled(true);
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1, 1);
                    new MessageManager("locking.block-locked")
                            .replace("%block%", LockManager.getName(block))
                            .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(LockManager.getLocker(block)))).getName())
                            .send(player);
                } else {
                    new MessageManager("locking.bypass.interact")
                            .replace("%block%", LockManager.getName(block))
                            .replace("%player%", Bukkit.getOfflinePlayer(UUID.fromString(Objects.requireNonNull(LockManager.getLocker(block)))).getName())
                            .send(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        // Cancel if entity interacts with locked blocked
        if (LockManager.isLocked(event.getBlock()) && Main.locked.getString(LockManager.getId(event.getBlock()) + ".locked") != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerEntityInteract(PlayerInteractEntityEvent event) {
        // Prevent renaming undead morphs
        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NAME_TAG) {
            for (String key : Main.morphs.getKeys(false)) {
                String value = Main.morphs.getString(key);
                if (value != null && value.contains(event.getRightClicked().getUniqueId().toString())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
