package me.bristermitten.warzone.game.init;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.Warzone;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.pdc.ListDataType;
import me.bristermitten.warzone.data.pdc.PointDataType;
import me.bristermitten.warzone.loot.LootGenerator;
import me.bristermitten.warzone.loot.LootTable;
import me.bristermitten.warzone.util.Sync;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

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

    private void cleanupOldPoints(Chunk chunk, Collection<Point> existingPoints) {
        if (existingPoints != null) { // Server might have crashed, clean up from the last time
            for (Point point : existingPoints) {
                Location location = point.toLocation(chunk.getWorld());
                Block loc = location.getBlock();
                Sync.run(() -> loc.setType(Material.AIR, false), plugin);
            }
        }
    }

    private List<Point> readPointsFromPDC(Chunk chunk) {
        return chunk.getPersistentDataContainer().get(KEY, new ListDataType<>(PointDataType.INSTANCE));
    }

    public void fill(Chunk chunk, LootTable table, float chestChance) {
        var oldChestPoints = readPointsFromPDC(chunk);
        cleanupOldPoints(chunk, oldChestPoints);
        var snapshot = chunk.getChunkSnapshot(true, false, false);

        Future.of(() -> BlockFinder.getBlocks(snapshot)
                .filter(point -> snapshot.getBlockType(point.x(), point.y(), point.z()).isAir()) // we can only place chests where there's air
                .filter(point -> snapshot.getBlockType(point.x(), point.y() + 1, point.z()).isAir()) // has to have a free spot above it
                .filter(point -> snapshot.getBlockType(point.x(), point.y() - 1, point.z()).isSolid())  //must have a solid block below it
                .filter(block -> ThreadLocalRandom.current().nextDouble() * 100f < chestChance)
                .toList())
                .flatMap(points -> Sync.run(() -> {
                    points.forEach(point -> {
                        var block = chunk.getWorld().getBlockAt(point.toLocation(chunk.getWorld()));
                        block.setType(Material.CHEST, false);
                        BlockState state = block.getState();
                        if (!(state instanceof Chest chest)) {
                            throw new IllegalStateException("what the hell");
                        }
                        fill(table, chest);
                    });

                    chunk.getPersistentDataContainer().set(KEY,
                            new ListDataType<>(PointDataType.INSTANCE),
                            points);
                }, plugin));
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
