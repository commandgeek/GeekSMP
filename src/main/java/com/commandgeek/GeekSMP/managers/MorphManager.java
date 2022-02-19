package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;

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

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;


public class MorphManager {
    public static Map<Player, Player> trackedPlayers = new Hashtable<>();
    public static Map<Player, BukkitTask> morphTasks = new HashMap<>();

    Player player;
    public MorphManager(Player player) {
        this.player = player;
    }

    public void morph(EntityType type) {
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
        entity.setCustomName(ChatColor.valueOf(Main.config.getString("groups." + TeamManager.getLast() + ".color")) + player.getName());
        entity.setCustomNameVisible(true);
        entity.setRemoveWhenFarAway(false);

        player.setGameMode(GameMode.ADVENTURE);
        EntityManager.hideEntity(entity, player);
        EntityManager.hidePlayerForAll(player);
        universalMorphTask(player);

        if (!MorphManager.isMorphedPersistent(player)) {
            new MessageManager("morph").replace("%morph%", type.toString().toLowerCase()).send(player);
            player.setFoodLevel(20);
            effect(player);
        }

        if (entity.getType() == EntityType.ZOMBIE) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000, 2, true, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 1, true, false, false));
            Zombie zombie = (Zombie) entity;
            zombie.setAdult();
        }

        if (entity.getType() == EntityType.SKELETON) {
            if (inventory.getItem(0) != null && !inventory.getItem(0).isSimilar(skeletonBow())) {
                inventory.addItem(inventory.getItem(0));
            }
            if (inventory.getItem(27) != null && !inventory.getItem(27).isSimilar(skeletonArrow())) {
                inventory.addItem(inventory.getItem(27));
            }

            inventory.setItem(0,skeletonBow());
            inventory.setItem(27, skeletonArrow());
        }

        Main.morphs.set(player.getUniqueId().toString(), entity.getUniqueId().toString());
        ConfigManager.saveData("morphs.yml", Main.morphs);
        Main.morphed.set(player.getUniqueId().toString(), entity.getType().toString());
        ConfigManager.saveData("morphed.yml", Main.morphed);
    }

    public void unmorph(boolean persistent) {
        PlayerInventory inventory = player.getInventory();
        Entity entity = getEntity(player);

        trackedPlayers.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
        if (entity != null) {
            entity.remove();
            player.removePotionEffect(PotionEffectType.SPEED);

            Main.morphs.set(player.getUniqueId().toString(), null);
            ConfigManager.saveData("morphs.yml", Main.morphs);
            if (persistent) {
                Main.morphed.set(player.getUniqueId().toString(), null);
                ConfigManager.saveData("morphed.yml", Main.morphed);
            }

            if (entity.getType() == EntityType.SKELETON) {
                if (inventory.getItem(0) != null && !inventory.getItem(0).isSimilar(skeletonBow())) {
                    inventory.addItem(inventory.getItem(0));
                }
                if (inventory.getItem(27) != null && !inventory.getItem(27).isSimilar(skeletonArrow())) {
                    inventory.addItem(inventory.getItem(27));
                }

                inventory.setItem(0, new ItemStack(Material.AIR));
                inventory.setItem(27, new ItemStack(Material.AIR));

                player.removePotionEffect(PotionEffectType.SPEED);
            }

            if (entity.getType() == EntityType.ZOMBIE) {
                player.removePotionEffect(PotionEffectType.SLOW);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }

        if (MorphManager.morphTasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(MorphManager.morphTasks.get(player).getTaskId());
            MorphManager.morphTasks.remove(player);
        }
    }

    public static ItemStack skeletonBow() {
        return new ItemManager(Material.BOW)
                .name("&dSkeleton Bow")
                .lore("&7Power I")
                .lore("&7Infinity")
                .enchant(Enchantment.ARROW_DAMAGE,1)
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

    public static void effect(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1,2);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0, 0.5, 0), 20, 0.2, 0.2, 0.2, 0.2);
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

        LivingEntity entity = (LivingEntity) MorphManager.getEntity(player);
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
            message = new MessageManager("tracking-empty").string();
        } else if (player.getWorld() == tracked.getWorld()){
            message = new MessageManager("tracking-player")
                    .replace("%player%", tracked.getName())
                    .replace("%distance%", String.valueOf(Math.round(player.getLocation().distance(tracked.getLocation()))))
                    .string();
        } else {
            message = new MessageManager("tracking-player")
                    .replace("%player%", tracked.getName())
                    .replace("%distance%m", "Different World")
                    .replace("%distance%", "Different World")
                    .string();
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

/*    public static void pet(OfflinePlayer op, Player player) {
        List<String> owners = Main.pets.getStringList(op.getUniqueId().toString());
        if (!owners.contains(player.getUniqueId().toString()) && player.getUniqueId() != op.getUniqueId()) {
            owners.add(player.getUniqueId().toString());
            Main.pets.set(op.getUniqueId().toString(), owners);
            ConfigManager.saveData("pets.yml", Main.pets);
            new MessageManager("pet-success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("pet-fail")
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
            new MessageManager("unpet-success")
                    .replace("%player%", op.getName())
                    .send(player);
        } else {
            new MessageManager("unpet-fail")
                    .replace("%player%", op.getName())
                    .send(player);
        }
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
*/
}
