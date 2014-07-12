package com.tenjava.entries.ewized.t3.module;

import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ListenerBuilder {
    List<Class<? extends Listener>> listeners = new ArrayList<>();
    List<Listener> registered = new ArrayList<>();

    /** Builder to add Listeners */
    public ListenerBuilder add(Class<? extends Listener> listener) {
        listeners.add(listener);
        return this;
    }

    /** Register each listener */
    public void register() {
        listeners.forEach(listener -> {
            try {
                Listener listen = listener.newInstance();
                Bukkit.getPluginManager().registerEvents(listen, TenJava.get());
                registered.add(listen);
                Common.debug(listener.getTypeName() + " registered listener.");
            } catch (IllegalAccessException | InstantiationException e) {
                Common.debug(e, true);
            }
        });
    }

    /** Unregister each listener */
    public void unregister() {
        registered.forEach(HandlerList::unregisterAll);
    }
}
