package com.commandgeek.geeksmp.managers;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListManager {

    public static List<Material> getMaterialList(String type) {
        List<Material> result = new ArrayList<>();
        if (Objects.equals(type, "pressure_plate")) {
            result.add(Material.ACACIA_PRESSURE_PLATE);
            result.add(Material.BIRCH_PRESSURE_PLATE);
            result.add(Material.CRIMSON_PRESSURE_PLATE);
            result.add(Material.JUNGLE_PRESSURE_PLATE);
            result.add(Material.OAK_PRESSURE_PLATE);
            result.add(Material.SPRUCE_PRESSURE_PLATE);
            result.add(Material.STONE_PRESSURE_PLATE);
            result.add(Material.WARPED_PRESSURE_PLATE);
            result.add(Material.DARK_OAK_PRESSURE_PLATE);
            result.add(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
            result.add(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
            result.add(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE);
        }
        if (Objects.equals(type, "boat")) {
            result.add(Material.ACACIA_BOAT);
            result.add(Material.BIRCH_BOAT);
            result.add(Material.DARK_OAK_BOAT);
            result.add(Material.JUNGLE_BOAT);
            result.add(Material.OAK_BOAT);
            result.add(Material.SPRUCE_BOAT);
        }
        return result;
    }

    public static List<EntityType> getEntityTypeList(String type) {
        List<EntityType> result = new ArrayList<>();
        if (Objects.equals(type, "minecart")) {
            result.add(EntityType.MINECART);
            result.add(EntityType.MINECART_FURNACE);
            result.add(EntityType.MINECART_COMMAND);
            result.add(EntityType.MINECART_HOPPER);
            result.add(EntityType.MINECART_TNT);
            result.add(EntityType.MINECART_MOB_SPAWNER);
            result.add(EntityType.MINECART_CHEST);
        }
        return result;
    }
}
