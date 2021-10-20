package me.bristermitten.warzone.game;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.repository.MutableGameRepository;
import me.bristermitten.warzone.game.state.GameStateManager;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.game.world.GameWorldUpdateTask;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

@Singleton
public class GameManagerImpl implements GameManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);


    private final ArenaManager arenaManager;
    private final GameWorldUpdateTask gameWorldUpdateTask;
    private final GameFactory gameFactory;
    private final MutableGameRepository gameStorage;
    private final GameStateManager gameStateManager;
    private final GameCleanupService gameCleanupService;

    @Inject
    public GameManagerImpl(
            ArenaManager arenaManager,
            GameWorldUpdateTask gameWorldUpdateTask,
            GameFactory gameFactory,
            MutableGameRepository gameStorage,
            GameStateManager gameStateManager,
            GameCleanupService gameCleanupService) {

        this.arenaManager = arenaManager;
        this.gameWorldUpdateTask = gameWorldUpdateTask;
        this.gameFactory = gameFactory;
        this.gameStorage = gameStorage;
        this.gameStateManager = gameStateManager;
        this.gameCleanupService = gameCleanupService;
    }


    @Override
    public @NotNull Game createNewGame(@NotNull Arena arena, @NotNull PartySize acceptedSize) {
        arenaManager.use(arena);

        var game = gameFactory.createGame(arena, Collections.emptySet(), acceptedSize);
        gameStorage.add(game);

        game.getPreGameLobbyTimer().addCompletionHook(() -> gameStateManager.setState(game, GameStates::inProgressState));

        LOGGER.debug("Created new game in arena {} with size {}", arena.name(), acceptedSize); //NOSONAR the record call is unbelievably cheap

        gameWorldUpdateTask.start();
        /* TODO i'm not really sure if this belong here. iterating an empty set is so cheap
         *   that it could just be started when the plugin loads up. but this seems like a kinda logical place to put it, for now */

        return game;
    }

    @Override
    @NotNull
    public Future<Unit> unload(@NotNull final Game game) {
        return gameCleanupService.scheduleCleanup(game);
    }


}
