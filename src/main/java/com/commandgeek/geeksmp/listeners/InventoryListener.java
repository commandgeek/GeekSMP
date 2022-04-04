package com.commandgeek.geeksmp.listeners;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.MorphManager;
import com.commandgeek.geeksmp.managers.TeamManager;
import com.commandgeek.geeksmp.menus.JoinMenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (TeamManager.isUndead(player)) {

            // If in menu, don't let them manipulate inventory
            if (!MorphManager.isMorphedPersistent(player)) {
                event.setCancelled(true);
                JoinMenu.select(player, event.getSlot());
            }

            // If morphed and skeleton, don't let them move bow/arrow
            if (MorphManager.isMorphedPlayer(player)) {
                for (String key : Main.morphs.getKeys(false)) {
                    String value = Main.morphs.getString(key);
                    if (value != null) {
                        UUID uuid = UUID.fromString(value);
                        Entity entity = Bukkit.getEntity(uuid);
                        if (entity instanceof Skeleton) {
                            if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
                                boolean skeletonBow = event.getCurrentItem().isSimilar(MorphManager.skeletonBow());
                                boolean skeletonArrow = event.getCurrentItem().isSimilar(MorphManager.skeletonArrow());
                                if ((skeletonBow && slot == 0) || (skeletonArrow && (slot == 27 || event.getClick() == ClickType.NUMBER_KEY))) {
                                    event.setCancelled(true);
                                }
                            }
                            if (event.getHotbarButton() == 0) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onOffhand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (MorphManager.isMorphedPlayer(player)) {
            for (String key : Main.morphs.getKeys(false)) {
                String value = Main.morphs.getString(key);
                if (value != null) {
                    UUID uuid = UUID.fromString(value);
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity instanceof Skeleton) {
                        if (event.getOffHandItem() != null && event.getOffHandItem().getItemMeta() != null) {
                            boolean skeletonBow = event.getOffHandItem().isSimilar(MorphManager.skeletonBow());
                            boolean skeletonArrow = event.getOffHandItem().isSimilar(MorphManager.skeletonArrow());
                            if (skeletonBow || skeletonArrow) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        // If morphed and skeleton, don't let them drop bow/arrow
        if (TeamManager.isUndead(player) && MorphManager.isMorphedPlayer(player) && (item.isSimilar(MorphManager.skeletonBow()) || item.isSimilar(MorphManager.skeletonArrow()))) {
            for (String key : Main.morphs.getKeys(false)) {
                String value = Main.morphs.getString(key);
                if (value != null) {
                    UUID uuid = UUID.fromString(value);
                    Entity entity = Bukkit.getEntity(uuid);
                    if (entity instanceof Skeleton) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (TeamManager.isUndead(player) && !MorphManager.isMorphedPersistent(player) && !player.isDead()) {
            new BukkitRunnable() {
                public void run() {
                    JoinMenu.open(player);
                }
            }.runTaskLater(Main.instance, 0);
        }
    }

    @EventHandler
    public void onPickUpItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (TeamManager.isUndead(player) && !MorphManager.isMorphedPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }
}
