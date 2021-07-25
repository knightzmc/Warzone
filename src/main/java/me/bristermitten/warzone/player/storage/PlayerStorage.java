package me.bristermitten.warzone.player.storage;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vavr.collection.HashMap;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.util.NoOp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.function.Consumer;

@Singleton
public class PlayerStorage implements Persistence {
    private final Cache<UUID, WarzonePlayer> playerCache = CacheBuilder.newBuilder().build();

    private final PlayerPersistence delegate;

    @Inject
    public PlayerStorage(PlayerPersistence delegate) {
        this.delegate = delegate;
    }

    public Future<WarzonePlayer> load(@NotNull UUID id) {
        WarzonePlayer cached = playerCache.getIfPresent(id);
        if (cached != null) {
            return Future.successful(cached);
        }
        return delegate.load(id)
                .onSuccess(loaded -> playerCache.put(id, loaded));
    }


    public void loadPlayer(@NotNull UUID id, Consumer<WarzonePlayer> callback) {
        load(id)
                .onFailure(t -> {
                    throw new StorageException("Could not load player data for " + id, t);
                })
                .onSuccess(callback);
    }

    @Override
    public @NotNull Future<Void> initialise() {
        return Future.run(NoOp.runnable());
    }

    /**
     * Retrieve a cached player if it exists, otherwise null.
     * This method should <b>NOT</b> perform any database queries or file operations
     */
    @Nullable
    public WarzonePlayer fetch(UUID id) {
        return playerCache.getIfPresent(id);
    }

    @Override
    public Future<Void> cleanup() {
        playerCache.cleanUp();
        return Future.sequence(HashMap.ofAll(playerCache.asMap())
                .values()
                .map(delegate::save))
                .map(discard -> null); // we don't care about the resultant Seq<Void>
    }
}
