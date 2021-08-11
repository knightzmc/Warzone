package me.bristermitten.warzone.arena;

import io.vavr.collection.List;
import me.bristermitten.warzone.config.ConfigurationProvider;
import me.bristermitten.warzone.party.Party;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

@Singleton
public class ArenaManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<Arena> arenas;

    private final Set<Arena> arenasInUse = new HashSet<>();

    @Inject
    public ArenaManager(ArenaLoader loader, ConfigurationProvider<ArenasConfig> configProvider) {
        configProvider.addInvalidationHook(unused -> logger.warn("arenas.yml was updated but changes will not apply until a restart"));

        this.arenas = List.ofAll(loader.loadArenas(configProvider.get()));
    }

    public boolean partyCanUseArena(Party party, Arena arena) {
        if (arena.permission() == null) {
            return true;
        }
        return party.getAllMembers().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .allMatch(p -> p.hasPermission(arena.permission()));
    }

    public Predicate<Arena> arenaIsInUse() {
        return arenasInUse::contains;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public void use(Arena arena) {
        if (!arenasInUse.add(arena)) {
            throw new IllegalStateException("Arena " + arena + " already in use!");
        }
    }

    public void free(Arena arena) {
        arenasInUse.remove(arena);
    }

}
