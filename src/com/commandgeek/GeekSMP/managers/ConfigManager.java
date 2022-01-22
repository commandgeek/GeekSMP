package com.commandgeek.GeekSMP.managers;

import com.commandgeek.GeekSMP.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private static final File dataFolder = Main.instance.getDataFolder();
    private static final File dataStorage = new File(dataFolder, "data");

    @SuppressWarnings("unused")
    private static void ignore(boolean b) {}

    public static void createDefaultConfig(String name) {
        if (!dataFolder.exists())
            ignore(dataFolder.mkdir());
        File file = new File(dataFolder, name);
        if (file.exists())
            return;
        Main.instance.saveResource(name, false);
    }

    public static void createData(String name) {
        if (!dataFolder.exists())
            ignore(dataFolder.mkdir());
        if (!dataStorage.exists())
            ignore(dataStorage.mkdir());
        File file = new File(dataStorage, name);
        if (!file.exists()) {
            try {
                ignore(file.createNewFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static FileConfiguration loadConfig(String name) {
        File file = new File(dataFolder, name);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration loadData(String name) {
        File file = new File(dataStorage, name);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void saveData(String name, FileConfiguration data) {
        File file = new File(dataStorage, name);
        try {
            data.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Location getDefaultWorldLocation(FileConfiguration config, String key) {
        World world = Bukkit.getServer().getWorlds().get(0);
        Location loc = new Location(world, 0, 0, 0);
        loc.setX(config.getDouble(key + ".x"));
        loc.setY(config.getDouble(key + ".y"));
        loc.setZ(config.getDouble(key + ".z"));
        if (config.contains(key + ".yaw"))
            loc.setYaw((float) config.getDouble(key + ".yaw"));
        if (config.contains(key + ".pitch"))
            loc.setYaw((float) config.getDouble(key + ".pitch"));
        return loc;
    }
}
