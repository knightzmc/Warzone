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
                    ArenasConfig.ArenaConfig.GulagConfig gulagConfig,
                    Region playableArea,
                    LootTable lootTable,
                    ArenasConfig.ArenaConfig.GameConfiguration gameConfiguration) {


    public Option<World> getWorld() {
        return Option.of(Bukkit.getWorld(world));
    }
}
