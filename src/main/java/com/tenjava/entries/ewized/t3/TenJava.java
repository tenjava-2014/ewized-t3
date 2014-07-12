package com.tenjava.entries.ewized.t3;

import com.tenjava.entries.ewized.t3.module.ModuleManager;
import com.tenjava.entries.ewized.t3.modules.Diamonds;
import com.tenjava.entries.ewized.t3.modules.Doors;
import com.tenjava.entries.ewized.t3.modules.Lighting;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.plugin.java.JavaPlugin;

import static com.google.common.base.Preconditions.checkNotNull;

public class TenJava extends JavaPlugin {
    private static TenJava plugin;
    private ModuleManager modules;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        Common.debug("Debug Enabled");

        // register modules
        modules = new ModuleManager();
        modules.builder()
            .add(Doors.class)
            .add(Diamonds.class)
            .add(Lighting.class)
            .startAll();
    }

    @Override
    public void onDisable() {
        modules.builder().stopAll();
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
