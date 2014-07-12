package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@ModuleInfo(name = "Doors", listeners = {Doors.DoorListener.class})
public class Doors extends Module {

    public static class DoorListener implements Listener {
        @EventHandler
        public void onDoor(PlayerInteractEvent e) {
            Common.debug(e.getClickedBlock().toString());
            Material door = e.getClickedBlock().getState().getType();

            if (door == Material.WOOD_DOOR) {
                Common.debug("WOOD DOOR");
            }
        }
    }
}
