package me.bristermitten.warzone.arena;

import io.vavr.control.Option;
import me.bristermitten.warzone.data.Region;
import me.bristermitten.warzone.loot.LootTable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public record Arena(String name,
                    String world,
                    @Nullable String permission,
                    int priority,
                    ArenaConfig.GulagConfig gulagConfig,
                    Region playableArea,
                    LootTable lootTable,
                    ArenaConfig.GameConfig gameConfig) {


    public Option<World> getWorld() {
        return Option.of(Bukkit.getWorld(world));
    }

    /**
     * Gets the world of the arena, throwing an exception if it is not present
     *
     * @return The world of the arena
     * @throws IllegalStateException if the world is not present according to {@link Bukkit#getWorld(String)}
     */
    public World getWorldOrThrow() {
        return getWorld().getOrElseThrow(() -> new IllegalStateException("World for arena " + name + " does not exist!"));
    }
}
