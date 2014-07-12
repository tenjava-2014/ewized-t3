package com.tenjava.entries.ewized.t3;

import org.bukkit.plugin.java.JavaPlugin;

import static com.google.common.base.Preconditions.checkNotNull;

public class TenJava extends JavaPlugin {
    private static TenJava plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {

    }

    /**
     * Get this plugin's instance.
     * @throws java.lang.IllegalArgumentException
     */
    public static TenJava get() {
        checkNotNull(plugin);

        return plugin;
    }
}
