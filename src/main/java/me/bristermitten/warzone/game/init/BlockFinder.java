package me.bristermitten.warzone.game.init;

import io.vavr.collection.List;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.Region;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;

import java.util.stream.IntStream;
import java.util.stream.Stream;


public class BlockFinder {
    private BlockFinder() {

    }

    public static List<ChunkSnapshot> getLoadedChunks(World world, Region region) {
        var list = List.<ChunkSnapshot>empty();
        var minChunkX = region.min().x() >> 4;
        var minChunkZ = region.min().z() >> 4;
        var maxChunkZ = region.max().z() >> 4;
        var maxChunkX = region.max().x() >> 4;
        for (int x = minChunkX; x < maxChunkX; x++) {
            for (int z = minChunkZ; z < maxChunkZ; z++) {
                if (world.isChunkLoaded(x, z)) {
                    list = list.prepend(world.getChunkAt(x, z).getChunkSnapshot(true, false, false));
                }
            }
        }
        System.out.println(list);
        return list;
    }

    public static Stream<Point> getBlocks(ChunkSnapshot chunkSnapshot) {
        return IntStream.rangeClosed(0, 15)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(0, 15)
                        .boxed()
                        .flatMap(z -> IntStream.rangeClosed(0, chunkSnapshot.getHighestBlockYAt(x, z))
                                .mapToObj(y -> new Point(x, y, z))));
    }

}
