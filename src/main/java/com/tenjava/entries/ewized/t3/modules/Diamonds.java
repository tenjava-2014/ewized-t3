package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

/**
 * This module will randomly create Diamonds depending
 * on the what will happen in game.
 */
@ModuleInfo(name = "Diamonds", listeners = {Diamonds.class})
public class Diamonds extends Module implements Listener {
    private Random rand = Common.random;

    private ItemStack diamond() {
        return new ItemStack(Material.DIAMOND, rand.nextInt(63) + 1);
    }

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
                if (player instanceof Player) {
                    Player p = ((Player) player);
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
                if (player instanceof Player) {
                    Player p = ((Player) player);
                    p.sendMessage(Common.color("&7&m--&6&l Diamonds were created near you!"));
                    p.playSound(loc, Sound.ITEM_PICKUP, 100F, 100F);
                }
            }
        }
    }
}
