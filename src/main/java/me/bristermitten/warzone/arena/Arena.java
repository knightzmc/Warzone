package me.bristermitten.warzone.arena;

import io.vavr.control.Option;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

public record Arena(String name,
                    String world,
                    @Nullable String permission,
                    int priority) {

    public Option<World> getWorld() {
        return Option.of(Bukkit.getWorld(world));
    }
}
