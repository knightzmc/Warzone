package me.bristermitten.warzone.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public record WorldAngledPoint(String world, double x, double y, double z, float yaw, float pitch) {

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

}
