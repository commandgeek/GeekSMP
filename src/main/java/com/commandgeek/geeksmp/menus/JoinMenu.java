package com.commandgeek.geeksmp.menus;

import com.commandgeek.geeksmp.Main;
import com.commandgeek.geeksmp.managers.EntityManager;
import com.commandgeek.geeksmp.managers.InventoryManager;
import com.commandgeek.geeksmp.managers.ItemManager;

import com.commandgeek.geeksmp.managers.MorphManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class JoinMenu {
    public static void open(Player player) {
        EntityManager.hidePlayerForAll(player);
        String title = ChatColor.translateAlternateColorCodes('&', "&5Morph Selection");
        int size = 9;

        if (Main.config.contains("morph-menu.title")) {
            //noinspection ConstantConditions
            title = ChatColor.translateAlternateColorCodes('&', Main.config.getString("morph-menu.title"));
        }
        if (Main.config.contains("morph-menu.size")) {
            size = Main.config.getInt("morph-menu.size");
        }

        Inventory inventory = new InventoryManager(size, ChatColor.translateAlternateColorCodes('&', title))
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
        String material = Main.config.getString("morph-menu.items." + index + ".material");
        if (material != null) {
            ItemManager itemManager = new ItemManager(Material.matchMaterial(material));
            if (Main.config.contains("morph-menu.items." + index + ".name"))
                itemManager.name(Main.config.getString("morph-menu.items." + index + ".name"));
            if (Main.config.contains("morph-menu.items." + index + ".lore"))
                itemManager.paragraph(30, Main.config.getString("morph-menu.items." + index + ".lore"));
            if (Main.config.contains("morph-menu.items." + index + ".skull"))
                itemManager.head(Main.config.getString("morph-menu.items." + index + ".skull"));
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
