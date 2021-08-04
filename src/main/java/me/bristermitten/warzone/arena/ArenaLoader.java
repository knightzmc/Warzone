package me.bristermitten.warzone.arena;

import io.vavr.collection.HashMap;
import me.bristermitten.warzone.util.Null;

import java.util.List;

public class ArenaLoader {
    private static final int DEFAULT_PRIORITY = 0;

    public Arena load(String name, ArenasConfig.ArenaConfig config) {
        return new Arena(name, config.world(), config.permission(), Null.get(config.priority(), DEFAULT_PRIORITY));
    }

    public List<Arena> loadArenas(ArenasConfig config) {
        return HashMap.ofAll(config.arenas())
                .map(t -> t.apply(this::load))
                .sortBy(Arena::priority)
                .toJavaList();
    }
}
