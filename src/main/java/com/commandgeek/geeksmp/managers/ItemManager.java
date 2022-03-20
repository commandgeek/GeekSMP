package com.commandgeek.geeksmp.managers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ItemManager {

    final ItemStack item;

    public ItemManager(Material material) {
        item = new ItemStack(material);
    }

    public ItemManager name(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemManager lore(String lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> entries = new ArrayList<>();
            List<String> current = meta.getLore();
            if (current != null)
                entries.addAll(current);

            entries.add(ChatColor.translateAlternateColorCodes('&', lore));

            meta.setLore(entries);
            item.setItemMeta(meta);
        }
        return this;
    }

    public void paragraph(int width, String paragraph) {
        String translated = ChatColor.translateAlternateColorCodes('&', paragraph);
        String[] entries = translated.split(" ");
        StringBuilder lore = new StringBuilder();
        int i = 1;
        for (String entry : entries) {
            entry = entry + " ";
            if (lore.length() + entry.length() > width) {
                lore(lore.toString().trim());
                lore = new StringBuilder(ChatColor.getLastColors(lore.toString()));
            }
            lore.append(entry);
            i++;
            if (i > entries.length) {
                lore(lore.toString().trim());
            }
        }
    }

    public ItemManager enchant(Enchantment enchantment, int level) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(enchantment, level, true);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemManager flag(ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flag);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemManager unbreakable(boolean unbreakable) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
            item.setItemMeta(meta);
        }
        return this;
    }

    public void head(String value) {

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", value));
            Field field = null;

            try {
                field = meta.getClass().getDeclaredField("profile");
            } catch (NoSuchFieldException | SecurityException e) {
                e.printStackTrace();
            }

            if (field != null) {
                field.setAccessible(true);

                try {
                    field.set(meta, profile);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                item.setItemMeta(meta);
            }
        }
    }

    public ItemStack get() {
        return item;
    }
}
