package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Morph;
import com.commandgeek.GeekSMP.managers.MorphManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import com.commandgeek.GeekSMP.menus.JoinMenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
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
            if (MorphManager.isMorphedPlayer(player) && event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
                ItemStack item = event.getCurrentItem();
                if ((item.isSimilar(Morph.skeletonBow()) && slot == 0) || (item.isSimilar(Morph.skeletonArrow()) && slot == 27)) {
                   for (String key : Main.morphs.getKeys(false)) {
                        String value = Main.morphs.getString(key);
                        assert value != null;
                        UUID uuid = UUID.fromString(value);
                        Entity entity = Bukkit.getEntity(uuid);
                        if (entity instanceof Skeleton) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

/*    private void cancelUndeadInventoryClick(Player player) {
        player.closeInventory();
        player.sendTitle(" ", new MessageManager("item-pickup-forbidden").string(), 0, 20, 5);
        new BukkitRunnable() {
            public void run() {
                player.getInventory().setContents(player.getInventory().getContents());
            }
        }.runTaskLater(Main.instance, 0);
    }*/

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        // If morphed and skeleton, don't let them drop bow/arrow
        if (TeamManager.isUndead(player) && MorphManager.isMorphedPlayer(player) && (item.isSimilar(Morph.skeletonBow()) || item.isSimilar(Morph.skeletonArrow()))) {
            for (String key : Main.morphs.getKeys(false)) {
                String value = Main.morphs.getString(key);
                assert value != null;
                UUID uuid = UUID.fromString(value);
                Entity entity = Bukkit.getEntity(uuid);
                if (entity instanceof Skeleton) {
                    event.setCancelled(true);
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
}
