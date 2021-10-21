package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.statistic.GamePersistence;
import me.bristermitten.warzone.game.statistic.GameStatistic;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Set;

public class GameEndingServiceImpl implements GameEndingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameEndingServiceImpl.class);
    private final GameRepository gameRepository;
    private final PartyManager partyManager;
    private final GameWinnerHandler gameWinnerHandler;
    private final GamePersistence gamePersistence;
    private final GameCleanupService gameCleanupService;

    @Inject
    public GameEndingServiceImpl(GameRepository gameRepository,
                                 PartyManager partyManager,
                                 GameWinnerHandler gameWinnerHandler,
                                 GamePersistence gamePersistence,
                                 GameCleanupService gameCleanupService) {
        this.gameRepository = gameRepository;
        this.partyManager = partyManager;
        this.gameWinnerHandler = gameWinnerHandler;
        this.gamePersistence = gamePersistence;
        this.gameCleanupService = gameCleanupService;
    }

    @Override
    public @NotNull Future<Unit> end(@NotNull Game game) {
        game.getGameBorder().pause();
        game.getGameBossBar().setPaused(true);

        var winningParty = gameWinnerHandler.getWinner(game);
        LOGGER.debug("winningParty = {}", winningParty);
        saveGameStats(game, winningParty);

        var rewardOp = winningParty
                .map(p -> gameWinnerHandler.giveWinnerRewards(game, p))
                .getOrElse(Future.successful(Unit.INSTANCE));
        LOGGER.debug("rewardOp = {}", rewardOp);

        return rewardOp.flatMap(unit -> gameCleanupService.scheduleCleanup(game));
    }

    private void saveGameStats(Game game, Option<Party> winningParty) {
        var stats = new GameStatistic(
                game.getUuid(),
                game.getArena().name(),
                Instant.ofEpochMilli(game.getTimer().getStartTimeMillis()),
                Instant.now(),
                game.getPartiesInGame().flatMap(Party::getAllMembers).toJavaSet(),
                winningParty.map(Party::getAllMembers).map(Set::copyOf).getOrElse(Set.of()),
                game.getDeaths(),
                game.getPlayerInformation().mapValues(PlayerInformation::createStatistics).toJavaMap()
        );
        gamePersistence.save(stats);
        LOGGER.debug("Saved stats {}", stats);
    }

    @Override
    public boolean shouldEnd(@NotNull Game game) {
        var players = gameRepository.getPlayers(game);

        var stillAlive = players.filter(player -> player.getCurrentState() instanceof AliveState);
        var remainingParties = stillAlive.groupBy(partyManager::getParty);

        final boolean shouldEnd = remainingParties.size() <= 1;
        LOGGER.debug("Game {} should end = {}, remaining parties = {}", game, shouldEnd, remainingParties);
        return shouldEnd;
    }
}
