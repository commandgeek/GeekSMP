package com.commandgeek.geeksmp.managers;

import java.sql.Timestamp;
import java.util.Random;

public class NumberManager {

    public static String digits(int number, int digits) {
        StringBuilder result = new StringBuilder();
        for (int i = digits - 1; i > 0; i--) {
            if (number < Math.pow(10, i)) {
                result.insert(0, 0);
            } else {
                break;
            }
        }
        result.append(number);
        return result.toString();
    }

    public static int length(int number) {
        return (int) (Math.log10(number) + 1);
    }

    public static boolean stringIsDuration(String string) {
        try {
            Integer.parseInt(string.replaceAll("(^\\d+)(.*)", "$1"));
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static int randomInt() {
        return new Random().nextInt(9);
    }

    public static String getTimeSince(long milli) {
        return getTimeFrom(new Timestamp(System.currentTimeMillis()).getTime() - milli);
    }
    public static String getTimeFrom(long milli) {
        long days = Math.floorDiv(milli, 1000 * 60 * 60 * 24);
        long hours = Math.floorDiv(milli, 1000 * 60 * 60) - (days * 24);
        long minutes = Math.floorDiv(milli, 1000 * 60) - (days * 60 * 24) - (hours * 60);
        long seconds = Math.floorDiv(milli, 1000) - (days * 60 * 60 * 24) - (hours * 60 * 60) - (minutes * 60);

        String time = "";
        if (days > 0)
            time = days + "d ";
        if (days > 0 || hours > 0)
            time = time + hours + "h ";
        if (days > 0 || hours > 0 || minutes > 0)
            time = time + minutes + "m ";
        time = time + seconds + "s";
        return time;
    }
}
