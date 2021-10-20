package me.bristermitten.warzone.game.repository;

import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

import static io.vavr.collection.HashSet.ofAll;

/**
 * Stores all active {@link Game}s
 */
@Singleton
public class GameStorage implements MutableGameRepository {
    private final Set<Game> games = new HashSet<>();
    private final PlayerManager playerManager;

    @Inject
    public GameStorage(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public io.vavr.collection.@NotNull Set<Game> getGames() {
        return ofAll(games);
    }

    @Override
    public io.vavr.collection.@NotNull Set<WarzonePlayer> getPlayers(@NotNull Game game) {
        return game.getPlayersInGame()
                .map(playerManager::lookupPlayer)
                .filter(Option::isDefined)
                .map(Option::get);
    }

    @Override
    public void add(Game game) {
        games.add(game);
    }

    @Override
    public void remove(Game game) {
        games.remove(game);
    }
}
