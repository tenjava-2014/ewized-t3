package com.tenjava.entries.ewized.t3.util;

import com.tenjava.entries.ewized.t3.TenJava;
import org.bukkit.ChatColor;

import java.util.Random;
import java.util.logging.Level;

/** Common methods that are used through out plugin */
public final class Common {
    private static boolean debug;
    public static final int TICK = 20;
    public static final Random random = new Random();

    static {
        debug = Boolean.parseBoolean(System.getProperty("debug"));
    }

    /** Debug messages if JVM has debug flag */
    public static void debug(String message, Object... args) {
        if (debug) {
            TenJava.get().getLogger().log(Level.INFO, message, args);
        }
    }

    /** Debug stack trace messages */
    public static void debug(Exception e, boolean simple) {
        debug(e.getMessage());

        if (!simple) {
            for (StackTraceElement element : e.getStackTrace()) {
                debug(element.toString());
            }
        }
    }

    /** Color a string */
    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /** Percentage wrapper */
    public static float percent(int total, int position) {
        return (float) (((double) position / (double) total) * 100) / 100;
    }

    /** Debug */
    public static boolean isDebug() {
        return debug;
    }
}
