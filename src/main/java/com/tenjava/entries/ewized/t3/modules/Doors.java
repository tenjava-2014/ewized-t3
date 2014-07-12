package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Clocks;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
        //Common.debug(e.getClickedBlock().toString());
        Player player = e.getPlayer();

        Material door = e.getClickedBlock() == null ? null : e.getClickedBlock().getType();

        //Common.debug(door.toString());
        if (door != null && door.equals(Material.WOODEN_DOOR) && rand.nextInt(10) == 5 || Common.isDebug()) {
            //Common.debug("WOOD DOOR");

            // Cancle pending tasks
            if (playerTasks.containsKey(player)) {
                playerTasks.get(player).forEach(BukkitTask::cancel);
                playerTasks.remove(player);
                player.getActivePotionEffects().stream()
                    .map(PotionEffect::getType)
                    .forEach(player::removePotionEffect);
            }

            playerTasks.put(player, new DoorTasks(player, 5 * Common.TICK).run());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        Player player = e.getPlayer();

        if (playerTasks.containsKey(player)) {
            Location to = e.getTo().clone();

            to.setX(to.getBlockX() >= 0 ? to.getBlockX() + 0.5 : to.getBlockX() - 0.5);
            to.setZ(to.getBlockZ() >= 0 ? to.getBlockZ() + 0.5 : to.getBlockZ() - 0.5);

            e.setTo(to);

            player.playSound(to, Sound.ENDERMAN_TELEPORT, 100F, 100F);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playEffect(to, Effect.ENDER_SIGNAL, 5);
            }
        }
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

            player.sendMessage(Common.color("&7&m--&a&l You are being sucked into a random dimension!"));
        }

        public void tick(int position) {
            player.setExp(Common.percent(getTotal(), position));
            //Common.debug(Common.percent(getTotal(), position) + "");
        }

        public void last(int position) {
            List<World> worlds = Bukkit.getWorlds();
            Location loc = worlds.get(rand.nextInt(worlds.size())).getSpawnLocation();
            player.teleport(loc);

            String world = loc.getWorld().getEnvironment().name().toLowerCase().replaceAll("(\\.|\\-|_)", " ");
            world = world.equals("normal") ? "Minecraftia" : world;
            player.sendMessage(Common.color("&7&m--&a&l You just notice you are in &2&l&o" + world + "&a&l."));

            loc.getBlock().setTypeIdAndData(Material.WOODEN_DOOR.getId(), (byte) 2, false);
            loc.add(0, 1, 0).getBlock().setTypeIdAndData(Material.WOODEN_DOOR.getId(), (byte) 8, false);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.getWorld().strikeLightningEffect(loc);
            }

            playerTasks.remove(player);
        }
    }

}
