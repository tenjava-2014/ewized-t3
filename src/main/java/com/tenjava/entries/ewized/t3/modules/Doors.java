package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Clocks;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * This random event will treat the doors like dimension doors
 * When you use a door, you have a random chance to travel to
 * different dimensions.
 */
@ModuleInfo(name = "Doors", listeners = {Doors.class})
public class Doors extends Module implements Listener {
    private Random rand = Common.random;
    private Map<Player, List<BukkitTask>> playerTasks = new HashMap<>();

    @EventHandler
    public void onDoor(PlayerInteractEvent e) {
        Common.debug(e.getClickedBlock().toString());
        Player player = e.getPlayer();
        Material door = e.getClickedBlock().getType();

        Common.debug(door.toString());
        /*if (door == Material.WOODEN_DOOR) {
            Common.debug("WOOD DOOR");*/

            // Cancle pending tasks
            if (playerTasks.containsKey(player)) {
                playerTasks.get(player).forEach(BukkitTask::cancel);
                playerTasks.remove(player);
            }

            playerTasks.put(player, new DoorTasks(player, 5 * Common.TICK).run());
        //}
    }

    private class DoorTasks extends Clocks {
        private Player player;

        public DoorTasks(Player player, int time) {
            super(time);
            this.player = player;
        }

        public void first(int position) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getTotal(), 1, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, getTotal(), 1, true));

            // Create an explosion and lighting
            player.getWorld().createExplosion(player.getLocation(), 0F);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getWorld().strikeLightningEffect(player.getLocation());
            }
        }

        public void tick(int position) {
            player.setExp(Common.percent(getTotal(), position));
            //Common.debug(Common.percent(getTotal(), position) + "");
        }

        public void last(int position) {
            List<World> worlds = Bukkit.getWorlds();
            player.teleport(worlds.get(rand.nextInt(worlds.size())).getSpawnLocation());
        }
    }

}
