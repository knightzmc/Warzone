package me.bristermitten.warzone.arena;

import io.vavr.collection.HashMap;
import me.bristermitten.warzone.loot.LootTableManager;
import me.bristermitten.warzone.util.Null;

import javax.inject.Inject;
import java.util.List;

public class ArenaLoader {
    private static final int DEFAULT_PRIORITY = 0;

    private final LootTableManager manager;

    @Inject
    public ArenaLoader(LootTableManager manager) {
        this.manager = manager;
    }

    public Arena load(String name, ArenasConfig.ArenaConfig config) {
        var lootTable = manager.getTables().get(config.lootTable())
                .getOrElseThrow(() -> new IllegalStateException("No loot table named " + config.lootTable() + " defined!"));

        return new Arena(name,
                config.world(),
                config.permission(),
                Null.get(config.priority(), DEFAULT_PRIORITY),
                config.gulagConfig(),
                config.playableArea().realised(),
                lootTable,
                config.game());
    }


    public List<Arena> loadArenas(ArenasConfig config) {
        return HashMap.ofAll(config.arenas())
                .map(t -> t.apply(this::load))
                .sortBy(Arena::priority)
                .toJavaList();
    }
}
