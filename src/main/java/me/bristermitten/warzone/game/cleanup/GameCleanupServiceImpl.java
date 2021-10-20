package me.bristermitten.warzone.game.cleanup;

import com.google.inject.Inject;
import com.google.inject.Provider;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.game.repository.MutableGameRepository;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.util.Schedule;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

class GameCleanupServiceImpl implements GameCleanupService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameCleanupServiceImpl.class);

    private final Provider<GameConfig> gameConfigProvider;
    private final Schedule schedule;
    private final PlayerManager playerManager;
    private final me.bristermitten.warzone.game.state.GameStateManager gameStateManager;
    private final ArenaManager arenaManager;
    private final MutableGameRepository gameRepository;

    @Inject
    public GameCleanupServiceImpl(Provider<GameConfig> gameConfigProvider,
                                  Schedule schedule,
                                  PlayerManager playerManager,
                                  me.bristermitten.warzone.game.state.GameStateManager gameStateManager,
                                  ArenaManager arenaManager,
                                  MutableGameRepository gameRepository) {
        this.gameConfigProvider = gameConfigProvider;
        this.schedule = schedule;
        this.playerManager = playerManager;
        this.gameStateManager = gameStateManager;
        this.arenaManager = arenaManager;
        this.gameRepository = gameRepository;
    }

    public @NotNull Future<Unit> scheduleCleanup(@NotNull final Game game) {
        long time = TimeUnit.SECONDS.toMillis(gameConfigProvider.get().gameEndTimer());
        return schedule.runLater(time, () -> cleanup(game));
    }

    public void cleanup(@NotNull final Game game) {
        var players = gameRepository.getPlayers(game);
        LOGGER.debug("Cleaning up game {} (remaining players = {})", game, players);

        players.forEach(warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::inLobbyState));
        game.getGameBorder().remove();

        gameStateManager.setState(game, GameStates::idlingState);

        arenaManager.free(game.getArena());
        gameRepository.remove(game);
    }
}
