package com.commandgeek.geeksmp.managers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeManager {

    final ShapedRecipe recipe;

    public RecipeManager(String namespace, ItemStack item) {
        recipe = new ShapedRecipe(NamespacedKey.minecraft(namespace), item);
    }

    public RecipeManager shape(String... shape) {
        recipe.shape(shape);
        return this;
    }

    public RecipeManager set(char c, Material material) {
        recipe.setIngredient(c, material);
        return this;
    }

    public void register() {
        Bukkit.getServer().addRecipe(recipe);
    }
}
