package com.commandgeek.GeekSMP;

import com.commandgeek.GeekSMP.managers.*;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.commandgeek.GeekSMP.Main.morphed;

public class Morph {
    public static Map<Player, BukkitTask> morphTasks = new HashMap<>();

    public static void effect(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1,2);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 20, 0.2, 0.2, 0.2, 0.2);
    }

    public static void morph(Player player, EntityType type) {
        new MessageManager("morph").replace("%morph%", type.toString().toLowerCase()).send(player);
        EntityManager.hidePlayerForAll(player);
        effect(player);
        universalMorphTask(player);
        ConfigManager.saveData("morphed.yml", morphed);

        if (Objects.equals(type.toString(), "ZOMBIE")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false, false));
            Zombie zombie = (Zombie) new MorphManager(player).morph(EntityType.ZOMBIE);
            zombie.setAdult();
        }

        if (Objects.equals(type.toString(), "SKELETON")) {
            player.getInventory().setItem(0, skeletonBow());
            player.getInventory().setItem(27, skeletonArrow());
            new MorphManager(player).morph(EntityType.SKELETON);
        }
    }

    public static ItemStack skeletonBow() {
        return new ItemManager(Material.BOW)
                .name("&dSkeleton Bow")
                .lore("&5Power of the skeleton...")
                .lore("&5Infinite arrows!")
                .enchant(Enchantment.ARROW_INFINITE,1)
                .enchant(Enchantment.VANISHING_CURSE,1)
                .unbreakable(true)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .flag(ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }
    public static ItemStack skeletonArrow() {
        return new ItemManager(Material.ARROW)
                .name("&dSkeleton Arrow")
                .lore("&5Power of the skeleton...")
                .lore("&5Infinite arrows!")
                .enchant(Enchantment.VANISHING_CURSE,1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .get();
    }

    public static void universalMorphTask(Player player) {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                MorphManager.burnInSunlight(player);
                MorphManager.copyDataToMorph(player);
                MorphManager.trackNearestPlayer(player);
            }
        }.runTaskTimer(Main.instance, 0, 1);
        morphTasks.put(player, task);
    }
}
