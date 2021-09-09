package me.bristermitten.warzone.game.init;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.Warzone;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.pdc.ListDataType;
import me.bristermitten.warzone.data.pdc.PointDataType;
import me.bristermitten.warzone.loot.LootGenerator;
import me.bristermitten.warzone.loot.LootTable;
import me.bristermitten.warzone.util.Schedule;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ChunkChestFiller {
    private static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getPlugin(Warzone.class), "chunk_chests");
    private static final Logger LOGGER = LoggerFactory.getLogger(ChunkChestFiller.class);
    private final LootGenerator generator;
    private final SplittableRandom random = new SplittableRandom();
    private final Schedule schedule;

    @Inject
    public ChunkChestFiller(LootGenerator generator, Schedule schedule) {
        this.generator = generator;
        this.schedule = schedule;
    }

    private Future<Unit> cleanupOldPoints(Chunk chunk, final Collection<Point> existingPoints) {
        var snapshot = chunk.getChunkSnapshot();
        return Future.of(() -> {
            var chests = BlockFinder.getBlocks(snapshot)

                    .filter(point -> snapshot.getBlockType(point.x(), point.y(), point.z()) == Material.CHEST)
                    .collect(Collectors.toCollection(HashSet::new));
            if (existingPoints != null) {
                chests.addAll(existingPoints);
            }
            return chests;
        }).flatMap(schedule.runSync(points -> {
            for (Point existingPoint : points) {
                var location = existingPoint.toLocation(chunk);
                var block = location.getBlock();
                block.setType(Material.AIR);
            }
            LOGGER.debug("Cleaned up {} points in {}", existingPoints.size(), chunk);
        }));
    }

    private List<Point> readPointsFromPDC(Chunk chunk) {
        return chunk.getPersistentDataContainer().get(KEY, new ListDataType<>(PointDataType.INSTANCE));
    }

    public Future<Unit> clean(Chunk chunk) {
        return cleanupOldPoints(chunk, readPointsFromPDC(chunk));
    }

    /**
     * Removes any points that are directly adjacent to another point from a given set,
     * and returns a new, filtered set
     */
    private Set<Point> filterAdjacent(@Unmodifiable Set<Point> points) {
        var copy = new HashSet<>(points);
        copy.removeIf(point -> {
            for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN)) {
                Point relative = point.add(face.getModX(), face.getModY(), face.getModZ());
                if (points.contains(relative)) {
                    return true;
                }
            }
            return false;
        });
        return copy;
    }

    public void fill(Chunk chunk, LootTable table, float chestChance) {
        clean(chunk)
                .map(irrelevantScopePollution -> chunk.getChunkSnapshot(true, false, false))
                .flatMap(snapshot ->
                        Future.of(() -> BlockFinder.getBlocks(snapshot)
                                        .filter(point -> point.x() != 0 && point.x() != 15 && point.z() != 0 && point.z() != 15)
                                        .filter(point -> snapshot.getBlockType(point.x(), point.y(), point.z()).isAir()) // we can only place chests where there's air
                                        .filter(point -> snapshot.getBlockType(point.x(), point.y() + 1, point.z()).isAir()) // has to have a free spot above it
                                        .filter(point -> snapshot.getBlockType(point.x(), point.y() - 1, point.z()).isSolid())  //must have a solid block below it
                                        .filter(block -> ThreadLocalRandom.current().nextDouble(0, 100) < chestChance)
                                        .collect(Collectors.toSet()))
                                .map(this::filterAdjacent)
                                .flatMap(points -> schedule.runSync(() -> {
                                    points.forEach(point -> {
                                        var block = point.toLocation(chunk).getBlock();
                                        block.setType(Material.CHEST);
                                        BlockState state = block.getState();
                                        if (!(state instanceof Chest chest)) {
                                            throw new IllegalStateException("what the hell");
                                        }
                                        fill(table, chest);
                                    });
                                    LOGGER.debug("Filled {} with {} chests", chunk, points.size());
                                    chunk.getPersistentDataContainer().set(KEY,
                                            new ListDataType<>(PointDataType.INSTANCE),
                                            List.copyOf(points));
                                })));
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
