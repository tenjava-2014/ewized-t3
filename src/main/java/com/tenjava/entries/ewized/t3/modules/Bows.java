package com.tenjava.entries.ewized.t3.modules;

import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.*;

@ModuleInfo(name = "Bows", listeners = {Bows.class})
public class Bows extends Module implements Listener {
    private final int CHANCE = 10;
    private final Random rand = Common.random;
    Map<Integer, Player> enderPearls = new WeakHashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onBow(EntityShootBowEvent e) {
        World world = e.getEntity().getWorld();
        Vector vector = e.getProjectile().getVelocity();
        Location loc = e.getEntity().getLocation().clone().add(0, e.getEntity().getEyeHeight(), 0).add(0, 1.5, 0);
        Entity entity;

        switch (rand.nextInt(CHANCE)) {
            case 0:
                entity = world.spawnEntity(loc, EntityType.PRIMED_TNT);
                break;
            case 1:
                entity = world.spawnEntity(loc, EntityType.EGG);
                break;
            case 2:
                entity = world.spawnEntity(loc, EntityType.ENDER_PEARL);
                enderPearls.put( entity.getEntityId(), (Player) e.getEntity());
                break;
            case 3:
                entity = world.spawnEntity(loc, EntityType.FIREBALL);
                break;
            case 4:
                entity = world.spawnEntity(loc, EntityType.FIREWORK);
                break;
            default:
                entity = world.spawnEntity(loc, EntityType.ARROW);
        }

        entity.setVelocity(vector);

        //Common.debug(loc.toString());

        e.setProjectile(entity);
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (enderPearls.containsKey(e.getEntity().getEntityId())) {
            enderPearls.get(e.getEntity().getEntityId()).teleport(e.getEntity().getLocation());
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

        Player player = e.getPlayer();

        if (enderPearls.containsValue(player)) {
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
}
