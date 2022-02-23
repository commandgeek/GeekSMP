package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;


public class MorphManager {
    public static final Map<Player, Player> trackedPlayers = new Hashtable<>();
    public static final Map<Player, BukkitTask> morphTasks = new HashMap<>();

    public static void morph(Player player, EntityType type) {
        PlayerInventory inventory = player.getInventory();

        if (player.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            player.getWorld().setDifficulty(Difficulty.EASY);
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "DIFFICULTY CANNOT BE PEACEFUL. DIFFICULTY SET TO EASY.");
        }

        LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), type);
        entity.setAI(false);
        entity.setCanPickupItems(false);
        if (entity.getEquipment() != null)
            entity.getEquipment().clear();
        entity.setRemoveWhenFarAway(false);
        new BukkitRunnable() {
            public void run() {
                entity.setCustomName(ChatColor.valueOf(Main.config.getString("groups." + TeamManager.getLast() + ".color")) + player.getName());
                entity.setCustomNameVisible(true);
            }
        }.runTaskLater(Main.instance,1);

        player.setGameMode(GameMode.ADVENTURE);
        universalMorphTask(player, type);
        EntityManager.hideEntity(entity, player);
        EntityManager.hidePlayerForAll(player);

        if (!isMorphedPersistent(player)) {
            new MessageManager("morphing.morph").replace("%morph%", type.toString().toLowerCase()).send(player);
            player.setFoodLevel(20);
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1, 2);
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 20, 0.2, 0.2, 0.2, 0.2);
        }

        if (entity.getType() == EntityType.ZOMBIE) {
            Zombie zombie = (Zombie) entity;
            zombie.setAdult();
        }

        if (entity.getType() == EntityType.SKELETON) {
            //noinspection ConstantConditions
            if (inventory.getItem(0) != null && !inventory.getItem(0).isSimilar(skeletonBow())) {
                inventory.addItem(inventory.getItem(0));
            }
            //noinspection ConstantConditions
            if (inventory.getItem(27) != null && !inventory.getItem(27).isSimilar(skeletonArrow())) {
                inventory.addItem(inventory.getItem(27));
            }

            inventory.setItem(0, skeletonBow());
            inventory.setItem(27, skeletonArrow());
        }

        Main.morphs.set(player.getUniqueId().toString(), entity.getUniqueId().toString());
        ConfigManager.saveData("morphs.yml", Main.morphs);
        Main.morphed.set(player.getUniqueId().toString(), entity.getType().toString());
        ConfigManager.saveData("morphed.yml", Main.morphed);
    }

    public static void unmorph(Player player, boolean persistent) {
        // Variables
        PlayerInventory inventory = player.getInventory();
        Entity entity = getEntity(player);

        // Universal stuff
        trackedPlayers.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.removePotionEffect(PotionEffectType.SPEED);

        // Data file
        Main.morphs.set(player.getUniqueId().toString(), null);
        ConfigManager.saveData("morphs.yml", Main.morphs);

        // Cancel morphTasks
        if (morphTasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(morphTasks.get(player).getTaskId());
            morphTasks.remove(player);
        }

        // Persistent boolean variable
        if (persistent) {
            Main.morphed.set(player.getUniqueId().toString(), null);
            ConfigManager.saveData("morphed.yml", Main.morphed);
        }

        // Entity stuff
        if (entity != null) {
            entity.remove();

            // Skeleton stuff
            if (entity.getType() == EntityType.SKELETON) {
                //noinspection ConstantConditions
                if (inventory.getItem(0) != null && !inventory.getItem(0).isSimilar(skeletonBow())) {
                    inventory.addItem(inventory.getItem(0));
                }
                //noinspection ConstantConditions
                if (inventory.getItem(27) != null && !inventory.getItem(27).isSimilar(skeletonArrow())) {
                    inventory.addItem(inventory.getItem(27));
                }

                inventory.setItem(0, new ItemStack(Material.AIR));
                inventory.setItem(27, new ItemStack(Material.AIR));
            }

            // Zombie stuff
            if (entity.getType() == EntityType.ZOMBIE) {
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

    public static ItemStack skeletonBow() {
        return new ItemManager(Material.BOW)
                .name("&dSkeleton Bow")
                .lore("&7Power I")
                .lore("&7Infinity")
                .enchant(Enchantment.ARROW_DAMAGE, 1)
                .enchant(Enchantment.ARROW_INFINITE, 1)
                .enchant(Enchantment.VANISHING_CURSE, 1)
                .unbreakable(true)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .flag(ItemFlag.HIDE_UNBREAKABLE)
                .get();
    }

    public static ItemStack skeletonArrow() {
        return new ItemManager(Material.ARROW)
                .name("&dSkeleton Arrow")
                .enchant(Enchantment.VANISHING_CURSE, 1)
                .flag(ItemFlag.HIDE_ENCHANTS)
                .get();
    }

    public static void universalMorphTask(Player player, EntityType entity) {
        BukkitTask task = new BukkitRunnable() {
            public void run() {
                burnInSunlight(player);
                copyDataToMorph(player);
                trackNearestPlayer(player);
                if (!EntityManager.isPlayerNear(player.getLocation(), Main.config.getInt("settings.speed-radius"))) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 2, true, false, false));
                } else if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                }
                if (entity == EntityType.ZOMBIE) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false, false));
                }
            }
        }.runTaskTimer(Main.instance, 0, 1);
        morphTasks.put(player, task);
    }

    public static boolean isMorphedPersistent(Player player) {
        return Main.morphed.getKeys(false).contains(player.getUniqueId().toString());
    }

    public static EntityType getEntityTypePersistent(Player player) {
        EntityType entity = null;
        if (Main.morphed.contains(player.getUniqueId().toString())) {
            entity = EntityType.valueOf(Main.morphed.getString(player.getUniqueId().toString()));
        }
        return entity;
    }

    public static Entity getEntity(Player player) {
        Entity entity = null;
        if (Main.morphs.contains(player.getUniqueId().toString())) {
            String uuid = Main.morphs.getString(player.getUniqueId().toString());
            if (uuid != null) {
                entity = Bukkit.getEntity(UUID.fromString(uuid));
            }
        }
        return entity;
    }

    public static Player getPlayer(Entity entity) {
        Player player = null;
        for (String key : Main.morphs.getKeys(false)) {
            String value = Main.morphs.getString(key);
            if (value != null && value.equals(entity.getUniqueId().toString())) {
                UUID uuid = UUID.fromString(key);
                player = Bukkit.getPlayer(uuid);
            }
        }
        return player;
    }

    public static boolean isMorphedEntity(Entity entity) {
        for (String key : Main.morphs.getKeys(false)) {
            String value = Main.morphs.getString(key);
            if (value != null && value.equals(entity.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMorphedPlayer(Player player) {
        return Main.morphs.getKeys(false).contains(player.getUniqueId().toString());
    }

    public static void copyDataToMorph(Player player) {

        LivingEntity entity = (LivingEntity) getEntity(player);
        if (entity != null) {
            EntityEquipment equipment = player.getEquipment();
            if (entity.getEquipment() != null && equipment != null) {
                entity.getEquipment().setItemInMainHand(equipment.getItemInMainHand());
                entity.getEquipment().setItemInOffHand(equipment.getItemInOffHand());
                entity.getEquipment().setHelmet(equipment.getHelmet());
                entity.getEquipment().setChestplate(equipment.getChestplate());
                entity.getEquipment().setLeggings(equipment.getLeggings());
                entity.getEquipment().setBoots(equipment.getBoots());
            }
            entity.setFireTicks(player.getFireTicks());
            for (PotionEffect effect : entity.getActivePotionEffects()) {
                if (!player.hasPotionEffect(effect.getType()))
                    entity.removePotionEffect(effect.getType());
            }
            entity.addPotionEffects(player.getActivePotionEffects());
            if (player.getVehicle() != null) {
                player.getVehicle().addPassenger(entity);
            }
        }
    }

    public static void burnInSunlight(Player player) {
        if (!Main.config.getBoolean("settings.burn")) return;
        long time = player.getWorld().getTime();
        if ((time < 12600 || 23600 < time) && (player.getEquipment() == null || player.getEquipment().getHelmet() == null) && player.getLocation().getBlock().getLightFromSky() >= 15) {
            player.setFireTicks(20);
        }
    }

    public static void trackNearestPlayer(Player player) {
        Player tracked = null;
        if (trackedPlayers.containsKey(player)) {
            tracked = trackedPlayers.get(player);
        }
        String message;
        if (tracked == null) {
            message = new MessageManager("track.empty").string();
        } else if (player.getWorld() == tracked.getWorld()) {
            message = new MessageManager("track.player")
                    .replace("%player%", tracked.getName())
                    .replace("%distance%", String.valueOf(Math.round(player.getLocation().distance(tracked.getLocation()))))
                    .string();
        } else {
            message = new MessageManager("track.player")
                    .replace("%player%", tracked.getName())
                    .replace("%distance%m", "Different World")
                    .replace("%distance%", "Different World")
                    .string();
        }
        if (!BypassManager.check(player)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }

    public static void pet(OfflinePlayer op, Player player) {
        List<String> owners = Main.pets.getStringList(op.getUniqueId().toString());
        if (!owners.contains(player.getUniqueId().toString()) && player.getUniqueId() != op.getUniqueId()) {
            owners.add(player.getUniqueId().toString());
            Main.pets.set(op.getUniqueId().toString(), owners);
            ConfigManager.saveData("pets.yml", Main.pets);
            new MessageManager("pets.pet.success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("pets.pet.fail")
                    .replace("%player%", op.getName())
                    .send(player);
        }
    }

    public static void unpet(OfflinePlayer op, Player player) {
        List<String> owners = Main.pets.getStringList(op.getUniqueId().toString());
        if (owners.contains(player.getUniqueId().toString())) {
            owners.remove(player.getUniqueId().toString());
            Main.pets.set(op.getUniqueId().toString(), owners);
            ConfigManager.saveData("pets.yml", Main.pets);
            new MessageManager("pets.unpet.success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("pets.unpet.fail")
                    .replace("%player%", op.getName())
                    .send(player);
        }
    }

    public static boolean pets() {
        return Main.config.getBoolean("settings.pets");
    }

    public static boolean isPettedBy(OfflinePlayer player, OfflinePlayer owner) {
        List<String> owners = Main.pets.getStringList(player.getUniqueId().toString());
        return owners.contains(owner.getUniqueId().toString());
    }

    public static boolean isPetNearOwner(Player player) {
        List<String> owners = Main.pets.getStringList(player.getUniqueId().toString());
        for (Entity entity : player.getNearbyEntities(50, 50, 50)) {
            if (entity instanceof Player owner && owners.contains(owner.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }
}
