package me.bristermitten.warzone.arena;

import io.vavr.collection.HashMap;
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

    public ArenaConfig.GameConfig.BossBarConfig loadBossBarConfig(ArenasConfigDAO.ArenaConfigDAO.GameConfigDAO.BossBarConfigDAO configuration) {
        return new ArenaConfig.GameConfig.BossBarConfig(
                configuration.format(),
                Null.get(configuration.color(), BossBar.Color.WHITE),
                Null.get(configuration.style(), BossBar.Overlay.PROGRESS),
                Null.get(configuration.flags(), Set.<BossBar.Flag>of()).stream().filter(Objects::nonNull).collect(Collectors.toSet())
        );
    }

    public ArenaConfig.GameConfig loadGameConfig(ArenasConfigDAO.ArenaConfigDAO.GameConfigDAO configuration) {
        return new ArenaConfig.GameConfig(
                configuration.timeLimit(),
                configuration.playerLimit(),
                configuration.borderDamage(),
                configuration.borderDamageTime(),
                configuration.chestRate(),
                Null.get(configuration.maxGulagEntries(), 1),
                loadBossBarConfig(configuration.bossBarConfigDAO())
        );
    }


    public List<Arena> loadArenas(ArenasConfigDAO config) {
        return HashMap.ofAll(config.arenas())
                .map(t -> t.apply(this::load))
                .sortBy(Arena::priority)
                .toJavaList();
    }
}
