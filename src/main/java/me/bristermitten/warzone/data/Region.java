package me.bristermitten.warzone.data;

import me.bristermitten.warzone.util.Numbers;
import org.bukkit.Chunk;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a region
 * Max is exclusive
 */
public record Region(Point min, Point max) {

    public static Region realiseRegion(Region region) {
        var x = Numbers.minMax(region.min().x(), region.max().x());
        var y = Numbers.minMax(region.min().y(), region.max().y());
        var z = Numbers.minMax(region.min().z(), region.max().z());
        var min = new Point(
                x._1,
                y._1,
                z._1
        );
        var max = new Point(
                x._2,
                y._2,
                z._2
        );
        return new Region(min, max);
    }

    public static Region fromChunk(Chunk chunk) {
        return realiseRegion(new Region(
                        new Point(chunk.getX() << 4, chunk.getWorld().getMinHeight(), chunk.getZ() << 4),
                        new Point((chunk.getX() << 4) + 16, chunk.getWorld().getMaxHeight(), (chunk.getZ() << 4) + 16)
                )
        );
    }

    public Region realised() {
        return realiseRegion(this);
    }

    public Point random() {
        var random = ThreadLocalRandom.current();
        var x = random.nextInt(min().x(), max().x());
        var y = random.nextInt(min().y(), max().y());
        var z = random.nextInt(min().z(), max().z());

        return new Point(x, y, z);
    }

    /**
     * Takes 2 points and returns a Region constraining those points to be within this region
     * That is, if either of the points go past this region's min and max, they will be "trimmed"
     */
    public Region segment(Point segmentMin, Point segmentMax) {
        var realised = realiseRegion(new Region(segmentMin, segmentMax));
        var minX = Math.max(min.x(), realised.min.x());
        var maxX = Math.min(min.x(), realised.min.x());
        var minY = Math.max(min.y(), realised.min.y());
        var maxY = Math.min(min.y(), realised.min.y());
        var minZ = Math.max(min.z(), realised.min.z());
        var maxZ = Math.min(min.z(), realised.min.z());

        return new Region(
                new Point(minX, minY, minZ),
                new Point(maxX, maxY, maxZ)
        );
    }
}
