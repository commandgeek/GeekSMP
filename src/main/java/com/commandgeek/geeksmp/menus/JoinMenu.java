package com.commandgeek.geeksmp.menus;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.InventoryManager;
import com.commandgeek.geeksmp.managers.ItemManager;

import com.commandgeek.geeksmp.managers.MorphManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class JoinMenu {
    public static void open(Player player) {
        EntityManager.hidePlayerForAll(player);

        Inventory inventory = new InventoryManager(9, "Select Identity")
                .set(2, getItem(1))
                .set(4, getItem(2))
                .set(6, getItem(3))
                .get();

        // Open
        new BukkitRunnable() {
            public void run() {
                player.openInventory(inventory);
            }
        }.runTaskLater(Main.instance, 0);
    }

    private static ItemStack getItem(int index) {
        String material = Main.config.getString("morph-selection.item" + index + ".material");
        if (material != null) {
            ItemManager itemManager = new ItemManager(Material.matchMaterial(material));
            if (Main.config.contains("morph-selection.item" + index + ".name"))
                itemManager.name(Main.config.getString("morph-selection.item" + index + ".name"));
            if (Main.config.contains("morph-selection.item" + index + ".lore"))
                itemManager.paragraph(30, Main.config.getString("morph-selection.item" + index + ".lore"));
            if (Main.config.contains("morph-selection.item" + index + ".skull"))
                itemManager.head(Main.config.getString("morph-selection.item" + index + ".skull"));
            return itemManager.get();
        }
        return null;
    }

    public static void select(Player player, int slot) {
        if (slot == 2) {
            MorphManager.morph(player, EntityType.ZOMBIE);
            player.closeInventory();
        }
        if (slot == 6) {
            MorphManager.morph(player, EntityType.SKELETON);
            player.closeInventory();
        }
    }
}
