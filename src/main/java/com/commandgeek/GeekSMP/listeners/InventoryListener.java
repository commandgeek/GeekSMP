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
