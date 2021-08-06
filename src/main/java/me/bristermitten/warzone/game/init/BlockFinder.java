package me.bristermitten.warzone.game.init;

import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.data.Region;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.stream.IntStream;
import java.util.stream.Stream;


public class BlockFinder {
    private BlockFinder() {

    }

    public static Stream<Block> findBlocks(World world, Region region, Material type) {
        return findBlocks(world, region)
                .filter(block -> block.getType() == type);
    }

    public static Stream<Block> findBlocks(World world, Region region) {
        System.out.println(region);
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
