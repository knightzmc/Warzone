package me.bristermitten.warzone.player;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.vavr.collection.HashMap;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.util.NoOp;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;

@Singleton
public class PlayerStorage implements Persistence {
    private final Cache<UUID, WarzonePlayer> playerCache = CacheBuilder.newBuilder().build();

    private final PlayerPersistence delegate;

    @Inject
    public PlayerStorage(PlayerPersistence delegate) {
        this.delegate = delegate;
    }

    public Future<WarzonePlayer> load(UUID id) {
        WarzonePlayer cached = playerCache.getIfPresent(id);
        if (cached != null) {
            return Future.successful(cached);
        }
        return delegate.load(id)
                .onSuccess(loaded -> playerCache.put(id, loaded));
    }

    @Override
    public Future<Void> initialise() {
        return Future.run(NoOp.runnable());
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
