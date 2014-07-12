package com.tenjava.entries.ewized.t3.modules;

import com.google.common.collect.ImmutableSet;
import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ModuleInfo(name = "Explode", listeners = {Lighting.class})
public class Lighting extends Module implements Listener {
    private final Set<BlockFace> faces = ImmutableSet.of(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    private final Set<Material> badBlocks = ImmutableSet.of(Material.SOUL_SAND, Material.NETHER_BRICK, Material.NETHERRACK, Material.OBSIDIAN, Material.FIRE, Material.ENDER_STONE);
    private final Set<Material> noSpread = ImmutableSet.of(Material.AIR, Material.STONE, Material.BEDROCK, Material.WATER, Material.LAVA, Material.STATIONARY_WATER, Material.STATIONARY_LAVA);
    private final int KILL_LIMIT = 2048;
    private final int MOB_SPAWN = 10;
    private final int MOB_CHANCE = 150;
    private final Random rand = Common.random;
    private static Map<BlockVector, World> strikes = new ConcurrentHashMap<>();
    private static Map<BlockVector, World> spread = new ConcurrentHashMap<>();
    private BukkitTask clock;

    public void start() {
        clock = Bukkit.getScheduler().runTaskTimer(TenJava.get(), () -> {
            // strikes spawns random mobs
            strikes.forEach((block, world) -> {
                Location location = block.toLocation(world);
                switch (rand.nextInt(MOB_CHANCE)) {
                    case 0:
                        mobSpawn(location).getWorld().spawnEntity(location, EntityType.BAT);
                        break;
                    case 1:
                        mobSpawn(location).getWorld().spawnEntity(location, EntityType.CREEPER);
                        break;
                    case 2:
                        mobSpawn(location).getWorld().spawnEntity(location, EntityType.SKELETON);
                        break;
                    case 3:
                        mobSpawn(location).getWorld().spawnEntity(location, EntityType.WITCH);
                        break;
                    case 4:
                        mobSpawn(location).getWorld().spawnEntity(location, EntityType.MUSHROOM_COW);
                        break;
                }
                //Common.debug(location.toString());
            });

            // Spread blocks
            spread.forEach((blockVector, world) -> {
                Block block = blockVector.toLocation(world).getBlock();

                for (BlockFace face : faces) {
                    Block relative = block.getRelative(face);

                    // Remove block vector from spread
                    if (badBlocks.contains(relative.getType())) {
                        spread.remove(new BlockVector(relative.getLocation().toVector()));
                    }
                    // Add blocks to spread and change block
                    else if (!noSpread.contains(relative.getType()) && rand.nextInt(4) == 0) {
                        spread.put(new BlockVector(relative.getLocation().toVector()), relative.getWorld());
                        relative.setType(setBadBlock());
                    }
                    // Randomly clear out blocks to prevent take over
                    else if (spread.size() > KILL_LIMIT) {
                        spread.clear();
                        break;
                    }
                }
            });

            //Common.debug("STRIKES: " + strikes.size());
            //Common.debug("SPREAD: " + spread.size());

        }, 5 * Common.TICK, Common.TICK);

    }

    private Location mobSpawn(Location location) {
        Location newLoc = location.clone();

        return newLoc.add(
            (double) rand.nextInt(MOB_SPAWN),
            (double) rand.nextInt(MOB_SPAWN),
            (double) rand.nextInt(MOB_SPAWN)
        );
    }

    private Material setBadBlock() {
        List<Material> blocks = new ArrayList<>(badBlocks);
        Collections.shuffle(blocks);
        return blocks.get(rand.nextInt(blocks.size()));
    }

    public void stop() {
        clock.cancel();
    }

    @EventHandler
    public void onLighting(LightningStrikeEvent e) {
        // Add location to internal clock
        Location loc = e.getLightning().getLocation();
        BlockVector block = new BlockVector(loc.toVector());

        strikes.put(block, loc.getWorld());

        spread.put(block, loc.getWorld());

        //Common.debug(block.toString());
    }
}
