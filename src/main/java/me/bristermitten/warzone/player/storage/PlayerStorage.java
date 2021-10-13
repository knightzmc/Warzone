package me.bristermitten.warzone.player.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class PlayerStorage implements Persistence {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerStorage.class);
    private final Cache<UUID, WarzonePlayer> playerCache = CacheBuilder.newBuilder().build();

    private final PlayerPersistence persistence;
    private final PlayerLeaderboard leaderboard;

    @Inject
    public PlayerStorage(PlayerPersistence persistence, PlayerLeaderboard leaderboard) {
        this.persistence = persistence;
        this.leaderboard = leaderboard;
    }

    public Future<Void> flush() {
        LOGGER.debug("Flushing cache, contents = {}", playerCache);
        return persistence.flush(playerCache.asMap().values());
    }

    public Future<WarzonePlayer> load(@NotNull UUID id) {
        LOGGER.debug("Loading player {}", id);
        WarzonePlayer cached = playerCache.getIfPresent(id);
        if (cached != null) {
            LOGGER.debug("Player {} was in cache ({})", id, cached);
            return Future.successful(cached);
        }
        LOGGER.debug("Looking up {} in database", id);
        return lookup(id)
                .onSuccess(player -> LOGGER.debug("Retrieved player {} from database = {} ", id, player))
                .onSuccess(leaderboard::add); // I don't really like this being here but it means that the leaderboard will stay up to date without any external intervention
    }

    private Future<WarzonePlayer> lookup(@NotNull UUID id) {
        return persistence.load(id)
                .onFailure(t -> {
                    throw new StorageException("Could not load player data for " + id, t);
                })
                .onSuccess(loaded -> playerCache.put(id, loaded));
    }


    @Override
    public @NotNull Future<Void> initialise() {
        return persistence.getAll()
                .map(warzonePlayers -> {
                    warzonePlayers.forEach(p -> {
                        playerCache.put(p.getPlayerId(), p);
                        leaderboard.add(p);
                    });
                    return null;
                });
    }

    /**
     * Retrieve a cached player if it exists, otherwise null.
     * This method should <b>NOT</b> perform any database queries or file operations
     */
    @Nullable
    public WarzonePlayer fetch(@NotNull UUID id) {
        return playerCache.getIfPresent(id);
    }

    @Override
    public Future<Void> cleanup() {
        LOGGER.debug("Cleaning up cache ({})", playerCache);
        playerCache.cleanUp();
        return flush();
    }
}
