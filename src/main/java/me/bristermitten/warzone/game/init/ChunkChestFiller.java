package me.bristermitten.warzone.game.init;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.Warzone;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.Region;
import me.bristermitten.warzone.data.pdc.ListDataType;
import me.bristermitten.warzone.data.pdc.PointDataType;
import me.bristermitten.warzone.loot.LootGenerator;
import me.bristermitten.warzone.loot.LootTable;
import me.bristermitten.warzone.util.Sync;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Objects;
import java.util.SplittableRandom;

public class ChunkChestFiller {
    private static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getPlugin(Warzone.class), "chunk_chests");
    private final LootGenerator generator;
    private final SplittableRandom random = new SplittableRandom();

    private final Plugin plugin;

    @Inject
    public ChunkChestFiller(LootGenerator generator, Plugin plugin) {
        this.generator = generator;
        this.plugin = plugin;
    }

    public void fill(ChunkSnapshot chunkSnapshot, LootTable table, float chestChance) {
        var world = Bukkit.getWorld(chunkSnapshot.getWorldName());
        Objects.requireNonNull(world, "World cannot be null");

        Future.fromCompletableFuture(world.getChunkAtAsync(chunkSnapshot.getX(), chunkSnapshot.getZ()))
                .onSuccess(chunk -> {
                    var existingPoints = chunk.getPersistentDataContainer()
                            .get(KEY, new ListDataType<>(PointDataType.INSTANCE));

                    if (existingPoints != null) { // Server might have crashed, clean up from the last time
                        existingPoints.stream()
                                .map(point -> point.toLocation(chunk.getWorld()))
                                .map(Location::getBlock)
                                .forEach(loc -> Sync.run(() -> loc.setType(Material.AIR), plugin));
                    }


                    // generate the new points
                    var blockPoints = Future.of(() -> BlockFinder.findBlocks(chunk.getWorld(), Region.fromChunk(chunk), Material.AIR) // we can only place chests where there's air
                            .filter(block -> block.getLocation().getBlockY() <= block.getWorld().getHighestBlockYAt(block.getLocation()) + 1)
                            .filter(block -> block.getRelative(BlockFace.UP).getType().isAir()) // has to have a free spot above it
                            .filter(block -> !block.getRelative(BlockFace.DOWN).getType().isAir()) //must have a solid block below it
                            .sequential() // this should be small enough that parallel isn't needed and it means random can be used properly
                            .filter(block -> random.nextDouble() * 100f < chestChance)
                            .toList());

                    blockPoints.onSuccess(blocks -> Bukkit.getScheduler().runTask(plugin, () -> {
                        blocks.forEach(block -> {
                            block.setType(Material.CHEST);
                            BlockState state = block.getState();
                            if (!(state instanceof Chest chest)) {
                                throw new IllegalStateException("what the hell");
                            }
                            fill(table, chest);
                        });

                        chunk.getPersistentDataContainer().set(KEY,
                                new ListDataType<>(PointDataType.INSTANCE),
                                blocks.stream().map(Block::getLocation).map(Point::fromLocation).toList());
                    }));
                });
    }

    public void fill(LootTable table, Chest chest) {
        Inventory inventory = chest.getInventory();
        Collection<ItemStack> items = generator.generateLoot(table);
        for (ItemStack item : items) {
            var slot = random.nextInt(0, inventory.getSize());
            inventory.setItem(slot, item);
        }
    }
}
