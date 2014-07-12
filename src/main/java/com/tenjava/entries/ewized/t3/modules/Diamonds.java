package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This module will randomly create Diamonds depending
 * on the what will happen in game.
 */
@ModuleInfo(name = "Diamonds", listeners = {Diamonds.class})
public class Diamonds extends Module implements Listener {
    private Random rand = Common.random;
    private List<Player> delay = new ArrayList<>();
    private BukkitTask clock;

    @Override
    public void start() {
        clock = Bukkit.getScheduler().runTaskTimer(TenJava.get(), delay::clear, 5 * Common.TICK, Common.TICK);
    }

    @Override
    public void stop() {
        clock.cancel();
    }

    /** The diamond that is given with a random stack size */
    private ItemStack diamond() {
        return new ItemStack(Material.DIAMOND, rand.nextInt(63) + 1);
    }

    /** Simple quick check */
    private boolean random() {
        return rand.nextInt(10) == 5 || Common.isDebug();
    }

    /** Spawn an item where the item despawn */
    @EventHandler(ignoreCancelled = true)
    public void onDespawn(ItemDespawnEvent e) {
        if (random()) {
            Location loc = e.getLocation();

            loc.getWorld().dropItem(loc, diamond());

            for (Entity player : loc.getChunk().getEntities()) {
                if (player instanceof Player && !delay.contains(player)) {
                    Player p = ((Player) player);
                    delay.add(p);
                    p.sendMessage(Common.color("&7&m--&6&l Diamonds were created near you!"));
                    p.playSound(loc, Sound.ITEM_PICKUP, 100F, 100F);
                }
            }
        }
    }

    /** Shoot out a diamond */
    @EventHandler(ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent e) {
        if (random()) {
            e.setItem(diamond());
            Location loc = e.getBlock().getLocation();

            for (Entity player : loc.getChunk().getEntities()) {
                if (player instanceof Player && !delay.contains(player)) {
                    Player p = ((Player) player);
                    delay.add(p);
                    p.sendMessage(Common.color("&7&m--&6&l Diamonds were created near you!"));
                    p.playSound(loc, Sound.ITEM_PICKUP, 100F, 100F);
                }
            }
        }
    }
}
