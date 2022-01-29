package com.commandgeek.GeekSMP;

import com.commandgeek.GeekSMP.managers.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class Morph {

    public static Map<Player, BukkitTask> morphTasks = new HashMap<>();

    public static void effect(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1,2);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 20, 0.2, 0.2, 0.2, 0.2);
    }

    public static void zombie(Player player) {

        new MessageManager("morph").replace("%morph%", "Zombie").send(player);
        EntityManager.hidePlayerForAll(player);
        player.getInventory().setHeldItemSlot(4);

        effect(player);

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false, false));

        Zombie zombie = (Zombie) new MorphManager(player).morph(EntityType.ZOMBIE);
        zombie.setAdult();
        zombieTask(player);
    }
    public static void zombieTask(Player player) {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                MorphManager.burnInSunlight(player);
                MorphManager.copyDataToMorph(player);
                MorphManager.trackNearestPlayer(player);
            }
        }.runTaskTimer(Main.instance, 0, 1);
        morphTasks.put(player, task);
    }

    public static void skeleton(Player player) {

        new MessageManager("morph").replace("%morph%", "Skeleton").send(player);
        EntityManager.hidePlayerForAll(player);
        player.getInventory().setHeldItemSlot(4);

        effect(player);
        player.getInventory().addItem(new ItemStack(Material.BOW), new ItemStack(Material.ARROW));

        new MorphManager(player).morph(EntityType.SKELETON);
        skeletonTask(player);
    }
    public static void skeletonTask(Player player) {
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
