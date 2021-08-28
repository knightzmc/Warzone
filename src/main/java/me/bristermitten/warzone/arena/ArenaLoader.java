package me.bristermitten.warzone.arena;

import io.vavr.collection.HashMap;
import me.bristermitten.warzone.bossbar.BossBarConfig;
import me.bristermitten.warzone.loot.LootTableManager;
import me.bristermitten.warzone.util.Null;
import net.kyori.adventure.bossbar.BossBar;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ArenaLoader {
    private static final int DEFAULT_PRIORITY = 0;
    public static final double DEFAULT_MINIMUM_BORDER_SIZE = 15;

    private final LootTableManager manager;

    @Inject
    public ArenaLoader(LootTableManager manager) {
        this.manager = manager;
    }

    public Arena load(String name, ArenasConfigDAO.ArenaConfigDAO config) {
        var lootTable = manager.getTables().get(config.lootTable())
                .getOrElseThrow(() -> new IllegalStateException("No loot table named " + config.lootTable() + " defined!"));

        return new Arena(name,
                config.world(),
                config.permission(),
                Null.get(config.priority(), DEFAULT_PRIORITY),
                loadGulagConfig(config.gulagConfigDAO()),
                config.playableArea().realised(),
                lootTable,
                loadGameConfig(config.game()));
    }

    public ArenaConfig.GulagConfig loadGulagConfig(ArenasConfigDAO.ArenaConfigDAO.GulagConfigDAO config) {
        return new ArenaConfig.GulagConfig(
                config.fightingArea1(),
                config.fightingArea2(),
                config.spawnArea()
        );
    }


    public ArenaConfig.GameConfig loadGameConfig(ArenasConfigDAO.ArenaConfigDAO.GameConfigDAO configuration) {
        return new ArenaConfig.GameConfig(
                configuration.timeLimit(),
                configuration.playerLimit(),
                configuration.borderDamage(),
                Null.get(configuration.minBorderSize(), DEFAULT_MINIMUM_BORDER_SIZE),
                configuration.borderDamageTime(),
                configuration.chestRate(),
                Null.get(configuration.maxGulagEntries(), 1),
                configuration.bossBarConfigDAO().loadBossBarConfig()
        );
    }


    public List<Arena> loadArenas(ArenasConfigDAO config) {
        return HashMap.ofAll(config.arenas())
                .map(t -> t.apply(this::load))
                .sortBy(Arena::priority)
                .toJavaList();
    }
}
