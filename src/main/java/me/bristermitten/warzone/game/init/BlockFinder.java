package me.bristermitten.warzone.game.init;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.Region;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class BlockFinder {
    private BlockFinder() {

    }

    public static List<ChunkSnapshot> getChunks(World world, Region region) {
        var minChunkX = region.min().x() >> 4;
        var minChunkZ = region.min().z() >> 4;
        var maxChunkZ = region.max().z() >> 4;
        var maxChunkX = region.max().x() >> 4;
        var chunks = new LinkedList<ChunkSnapshot>();
        for (int x = minChunkX; x < maxChunkX; x++) {
            for (int z = minChunkZ; z < maxChunkZ; z++) {
                chunks.add(world.getEmptyChunkSnapshot(x, z, false, false));
            }
        }
        return chunks;
    }

    public static Stream<Block> findBlocks(World world, Region region, Material type) {
        return findBlocks(world, region)
                .filter(block -> block.getType() == type);
    }

    public static Stream<Block> findBlocks(World world, Region region) {
        return IntStream.range(region.min().x(), region.max().x())
                .boxed()
                .parallel()
                .flatMap(x -> IntStream.range(region.min().y(), region.max().y())
                        .parallel()
                        .boxed()
                        .flatMap(y -> IntStream.range(region.min().z(), region.max().z())
                                .parallel()
                                .mapToObj(z -> new Point(x, y, z))))
                .map(point -> point.toLocation(world))
                .map(Location::getBlock);
    }
}
