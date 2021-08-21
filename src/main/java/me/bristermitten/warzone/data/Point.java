package me.bristermitten.warzone.data;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public record Point(int x, int y, int z) {

    public static Point fromLocation(Location location) {
        return new Point(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    /**
     * Get a location in a given chunk based in this point
     * This method will fail if {@link Chunk#getBlock(int, int, int)} would fail
     *
     */
    public Location toLocation(Chunk chunk) {
        return chunk.getBlock(x, y, z).getLocation();
    }


    public Point add(int x, int y, int z) {
        return new Point(
                this.x + x,
                this.y + y,
                this.z + z
        );
    }

    public Point setY(int y) {
        return new Point(x, y, z);
    }
}
