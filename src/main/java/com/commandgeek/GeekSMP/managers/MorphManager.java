package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import com.commandgeek.GeekSMP.Morph;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import static com.commandgeek.GeekSMP.Main.morphed;

public class MorphManager {
    public static Map<Player, Player> trackedPlayers = new Hashtable<>();

    Player player;
    public MorphManager(Player player) {
        this.player = player;
    }

    public LivingEntity morph(EntityType type) {
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
        player.setFoodLevel(20);

        Main.morphs.set(player.getUniqueId().toString(), entity.getUniqueId().toString());
        ConfigManager.saveData("morphs.yml", Main.morphs);

        Main.morphed.set(player.getUniqueId().toString(), entity.getType().toString());
        ConfigManager.saveData("morphed.yml", Main.morphed);

        return entity;
    }

    public void unmorph(boolean persistent) {
        Entity entity = getEntity(player);
        trackedPlayers.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
        if (entity != null) {
            entity.remove();
            Main.morphs.set(player.getUniqueId().toString(), null);
            ConfigManager.saveData("morphs.yml", Main.morphs);
            if (persistent) {
                Main.morphed.set(player.getUniqueId().toString(), null);
                ConfigManager.saveData("morphed.yml", morphed);
            }

            if (entity.getType() == EntityType.SKELETON) {
                player.getInventory().setItem(0, new ItemStack(Material.AIR));
                player.getInventory().setItem(27, new ItemStack(Material.AIR));
            }
        }

        if (Morph.morphTasks.containsKey(player)) {
            Bukkit.getScheduler().cancelTask(Morph.morphTasks.get(player).getTaskId());
            Morph.morphTasks.remove(player);
        }

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
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
