package com.tenjava.entries.ewized.t3.util;

import com.tenjava.entries.ewized.t3.TenJava;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public final class Common {
    private static boolean debug;

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
}
