package me.bristermitten.warzone.arena;

import me.bristermitten.warzone.config.Configuration;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record ArenasConfig(
        Map<String, ArenaConfig> arenas
) {
    public static final Configuration<ArenasConfig> CONFIG = new Configuration<>(ArenasConfig.class, "arenas.yml");

    public record ArenaConfig(String world,
                              @Nullable String permission,
                              @Nullable Integer priority) {
    }
}
