package com.commandgeek.GeekSMP.managers;

public class ServerManager implements Runnable {

    public static int tickCount = 0;
    public static long[] tickArray = new long[600];

    public static double getTPS() {
        return getTPS(100);
    }

    public static double getTPS(int ticks) {
        if (tickCount< ticks) {
            return 20.0D;
        }
        int target = (tickCount- 1 - ticks) % tickArray.length;
        long elapsed = System.currentTimeMillis() - tickArray[target];
        return ticks / (elapsed / 1000.0D);
    }

    public void run() {
        tickArray[(tickCount% tickArray.length)] = System.currentTimeMillis();
        tickCount++;
    }
}