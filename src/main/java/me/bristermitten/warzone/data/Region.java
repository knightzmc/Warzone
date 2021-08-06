package me.bristermitten.warzone.data;

import me.bristermitten.warzone.util.Numbers;
import org.bukkit.Chunk;

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
}
