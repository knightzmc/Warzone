package me.bristermitten.warzone.data;

import org.bukkit.Location;
import org.bukkit.World;

public record Point(int x, int y, int z) {

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public Point add(int x, int y, int z) {
        return new Point(
                this.x + x,
                this.y + y,
                this.z + z
        );
    }
}
