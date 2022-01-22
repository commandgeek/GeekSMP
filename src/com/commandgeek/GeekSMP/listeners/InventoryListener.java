package com.commandgeek.GeekSMP.listeners;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.managers.InventoryManager;
import com.commandgeek.GeekSMP.managers.MessageManager;
import com.commandgeek.GeekSMP.managers.MorphManager;
import com.commandgeek.GeekSMP.managers.TeamManager;
import com.commandgeek.GeekSMP.menus.JoinMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@SuppressWarnings({"unused"})
public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (TeamManager.isUndead(player) && !MorphManager.isMorphedPlayer(player)) {
            event.setCancelled(true);
            JoinMenu.select(player, event.getSlot());
        }

        /*
        if (TeamManager.isUndead(player)) {
            boolean hasSpace = InventoryManager.getContentAmount(player) <= InventoryManager.getEquipmentAmount(player);
            InventoryAction[] pickupActions = {InventoryAction.PICKUP_ALL, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME};
            InventoryAction[] placeActions = {InventoryAction.PLACE_ALL, InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME};
            InventoryType[] types = {InventoryType.PLAYER, InventoryType.CRAFTING, InventoryType.WORKBENCH, InventoryType.ANVIL, InventoryType.BEACON, InventoryType.BLAST_FURNACE, InventoryType.BREWING, InventoryType.CARTOGRAPHY, InventoryType.ENCHANTING, InventoryType.FURNACE, InventoryType.GRINDSTONE, InventoryType.LOOM, InventoryType.SMITHING, InventoryType.SMOKER};

            if (!Arrays.asList(placeActions).contains(event.getAction())) {
                if (event.getClickedInventory() != null && !Arrays.asList(types).contains(event.getClickedInventory().getType())) {
                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                        if (event.getCurrentItem().getAmount() > 1) {
                            if (Arrays.asList(pickupActions).contains(event.getAction()) && hasSpace) {

                                int slot = event.getSlot();
                                ItemStack item = event.getCurrentItem();
                                item.setAmount(item.getAmount() - 1);
                                event.getClickedInventory().setItem(slot, item);
                                item.setAmount(1);
                                player.getInventory().addItem(item);
                                event.setCancelled(true);

                            } else {
                                event.setCancelled(true);
                                cancelUndeadInventoryClick(player);
                            }
                        } else if (!(event.getAction() == InventoryAction.SWAP_WITH_CURSOR && event.getCurrentItem().getAmount() <= 1)) {
                            if (!hasSpace) {
                                event.setCancelled(true);
                                cancelUndeadInventoryClick(player);
                            }
                        }
                    }
                }
            }
        } */
    }

    private void cancelUndeadInventoryClick(Player player) {
        player.closeInventory();
        player.sendTitle(" ", new MessageManager("item-pickup-forbidden").string(), 0, 20, 5);
        new BukkitRunnable() {
            public void run() {
                player.getInventory().setContents(player.getInventory().getContents());
            }
        }.runTaskLater(Main.instance, 0);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (TeamManager.isUndead(player) && !MorphManager.isMorphedPlayer(player) && !player.isDead()) {
            new BukkitRunnable() {
                public void run() {
                    JoinMenu.open(player);
                }
            }.runTaskLater(Main.instance, 0);
        }
    }
}
