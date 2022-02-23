package com.commandgeek.geeksmp.managers;

import com.commandgeek.geeksmp.Main;

public class StatsManager {

    public static void add(String stat) {
        int joins = 1;
        if (Main.stats.contains(stat)) {
            joins += Main.stats.getInt(stat);
        }
        Main.stats.set(stat, joins);
        ConfigManager.saveData("stats.yml", Main.stats);
    }
}
