package com.commandgeek.GeekSMP.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryManager {

    Inventory gui;

    public InventoryManager(int size, String title) {
        this.gui = Bukkit.createInventory(null, size, title);
    }

    public InventoryManager set(int slot, ItemStack item) {
        gui.setItem(slot, item);
        return this;
    }

    public Inventory get() {
        return gui;
    }

    public static int getContentAmount(Player player) {
        int result = getContentAmount(player.getInventory());
        if (player.getItemOnCursor().getType() != Material.AIR)
            result++;
        return result;
    }
    public static int getContentAmount(Inventory inventory) {
        Player player = (Player) inventory.getHolder();
        int result = 0;
        if (player != null) {
            for (ItemStack item : inventory.getContents()) {
                if (item != null) {
                    if (!(item.getType() == Material.ARROW && MorphManager.getEntity(player) instanceof Skeleton))
                        result++;
                }
            }
        }
        return result;
    }

    public static int getEquipmentAmount(Player player) {
        return getEquipmentAmount(player.getEquipment());
    }
    public static int getEquipmentAmount(EntityEquipment equipment) {
        int result = 0;
        if (equipment == null)
            return result;
        if (equipment.getHelmet() != null)
            result++;
        if (equipment.getChestplate() != null)
            result++;
        if (equipment.getLeggings() != null)
            result++;
        if (equipment.getBoots() != null)
            result++;
        return result;
    }
}
