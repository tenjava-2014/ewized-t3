package com.tenjava.entries.ewized.t3.module;

import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleBuilder {
    List<Class<? extends Module>> modules = new ArrayList<>();
    Map<Module, ListenerBuilder> instances = new HashMap<>();

    /** Add the module */
    public ModuleBuilder add(Class<? extends Module> module) {
        modules.add(module);
        return this;
    }

    /** Start the modules */
    public void startAll() {
        modules.forEach(moduleClass -> {
            try {
                ModuleInfo info = moduleClass.getAnnotation(ModuleInfo.class);
                Module module = moduleClass.newInstance();

                ListenerBuilder builder = new ListenerBuilder();

                for (Class<? extends Listener> listener : info.listeners()) {
                    builder.add(listener);
                }

                instances.put(module, builder);

                module.start();
                builder.register();

                Common.debug(info.name() + " started.");
            } catch (IllegalAccessException | InstantiationException e) {
                Common.debug(e, true);
            }
        });
    }

    /** Stop the modules */
    public void stopAll() {
        instances.forEach((module, listener) -> {
            ModuleInfo info = module.getClass().getAnnotation(ModuleInfo.class);

            module.stop();
            listener.unregister();

            Common.debug(info.name() + " stoped.");
        });
    }
}
