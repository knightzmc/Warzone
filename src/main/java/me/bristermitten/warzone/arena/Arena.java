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

    public World forceGetWorld() {
        return getWorld().getOrElseThrow(() -> new IllegalStateException("World for arena " + name + " does not exist!"));
    }
}
