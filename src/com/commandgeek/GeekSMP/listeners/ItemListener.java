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

            /*
            if (TeamManager.isUndead(player)) {
                if (InventoryManager.getContentAmount(player) > InventoryManager.getEquipmentAmount(player)) {
                    if (!(event.getItem().getItemStack().getType() == Material.ARROW && MorphManager.getEntity(player) instanceof Skeleton)) {
                        player.sendTitle(" ", new MessageManager("item-pickup-forbidden").string(), 0, 5, 5);
                        event.setCancelled(true);
                        return;
                    }
                }
                if (event.getItem().getItemStack().getAmount() > 1) {
                    event.setCancelled(true);
                    ItemStack item = event.getItem().getItemStack();
                    item.setAmount(item.getAmount() - 1);
                    event.getItem().setItemStack(item);
                    item.setAmount(1);
                    player.getInventory().addItem(item);
                } else {
                    new PacketManager().hideEntity(event.getItem());
                }
            } */
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
