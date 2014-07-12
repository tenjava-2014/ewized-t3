package com.tenjava.entries.ewized.t3.modules;

import com.google.common.collect.ImmutableSet;
import com.tenjava.entries.ewized.t3.TenJava;
import com.tenjava.entries.ewized.t3.module.Module;
import com.tenjava.entries.ewized.t3.module.ModuleInfo;
import com.tenjava.entries.ewized.t3.util.Common;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

import java.util.*;

@ModuleInfo(name = "Explode", listeners = {Lighting.class})
public class Lighting extends Module implements Listener {
    private final Set<BlockFace> faces = ImmutableSet.of(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);
    private final Set<Material> badBlocks = ImmutableSet.of(Material.SOUL_SAND, Material.NETHER_BRICK, Material.NETHERRACK, Material.OBSIDIAN, Material.FIRE, Material.ENDER_STONE);
    private final int KILL_LIMIT = 2048;
    private final int MOB_SPAWN = 10;
    private final int MOB_CHANCE = 150;
    private final Random rand = Common.random;
    private static List<BlockVector> strikes = Collections.synchronizedList(new ArrayList<>());
    private static List<BlockVector> spread = Collections.synchronizedList(new ArrayList<>());
    private BukkitTask clock;

    public void start() {
        clock = Bukkit.getScheduler().runTaskTimer(TenJava.get(), () -> {
            // strikes spawns random mobs
            strikes.forEach(block -> {
                Location location = block.toLocation(Bukkit.getWorlds().get(0));
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
            List<BlockVector> threadSafe = new ArrayList<>(spread);
            spread.forEach(blockVector -> {
                Block block = blockVector.toLocation(Bukkit.getWorlds().get(0)).getBlock();

                for (BlockFace face : faces) {
                    Block relative = block.getRelative(face);

                    // Remove block vector from spread
                    if (badBlocks.contains(relative.getType())) {
                        threadSafe.remove(new BlockVector(relative.getLocation().toVector()));
                    }
                    // Add blocks to spread and change block
                    else if (!relative.getType().equals(Material.AIR) && rand.nextInt(10) == 5){
                        threadSafe.add(new BlockVector(relative.getLocation().toVector()));
                        relative.setType(setBadBlock());
                    }
                    // Randomly clear out blocks to prevent take over
                    else if (spread.size() > KILL_LIMIT) {
                        for (int i = 0; i < rand.nextInt(spread.size()); i++) {
                            threadSafe.remove(rand.nextInt(spread.size()));
                        }
                    }
                }
            });
            spread = threadSafe;

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
        BlockVector block = new BlockVector(e.getLightning().getLocation().toVector());

        strikes.add(block);

        spread.add(block);

        //Common.debug(block.toString());
    }
}
