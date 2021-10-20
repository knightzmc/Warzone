package me.bristermitten.warzone.game;

import io.vavr.collection.HashMap;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.statistic.GamePersistence;
import me.bristermitten.warzone.game.statistic.GameStatistic;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GameWinnerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameWinnerHandler.class);
    private final GameRepository gameRepository;
    private final PartyManager partyManager;
    private final GameCleanupService gameCleanupService;
    private final GamePersistence gamePersistence;
    private final XPHandler xpHandler;
    private final LangService langService;

    @Inject
    public GameWinnerHandler(GameRepository gameRepository, PartyManager partyManager, GameCleanupService gameCleanupService, GamePersistence gamePersistence, XPHandler xpHandler, LangService langService) {
        this.gameRepository = gameRepository;

        this.partyManager = partyManager;
        this.gameCleanupService = gameCleanupService;
        this.gamePersistence = gamePersistence;
        this.xpHandler = xpHandler;
        this.langService = langService;
    }

    public Future<Unit> checkForWinner(Game game) {
        var players = gameRepository.getPlayers(game);

        var stillAlive = players.filter(player -> player.getCurrentState() instanceof AliveState);
        var remainingParties = stillAlive.groupBy(partyManager::getParty);

        if (remainingParties.isEmpty()) {
            LOGGER.debug("Cleaning up game {} in arena {} because remainingParties is empty", game.getUuid(), game.getArena().name()); //NOSONAR
            //pack it up boys
            return gameCleanupService.scheduleCleanup(game);
        }
        if (remainingParties.size() != 1) {
            LOGGER.debug("Not doing anything, because remainingParties.size() != 1 ({})", remainingParties);
            // The game isn't over yet!
            return Future.successful(Unit.INSTANCE);
        }
        LOGGER.debug("Ending game, only 1 party remaining. remainingParties = {}, stillAlive = {}", remainingParties, stillAlive);

        game.getGameBorder().pause();
        game.getGameBossBar().setPaused(true);

        var winningParty = remainingParties.keySet().head();
        saveGameStats(game, winningParty);

        giveWinnerXP(game, players, stillAlive, winningParty);

        winningParty.getAllMembers().forEach(winnerId -> {
            var player = Bukkit.getPlayer(winnerId);
            Objects.requireNonNull(player); // if they're offline they should've been removed from the party
            langService.send(player, config -> config.gameLang().winner());
        });

        players.forEach(warzonePlayer -> warzonePlayer.getPlayer().peek(player ->
                langService.send(player,
                        config -> config.gameLang().winnerBroadcast(),
                        Map.of("{winner}", Objects.requireNonNull(Bukkit.getPlayer(winningParty.getOwner())).getName()))));
        return gameCleanupService.scheduleCleanup(game);
    }

    private void giveWinnerXP(Game game, io.vavr.collection.Set<WarzonePlayer> players, io.vavr.collection.Set<WarzonePlayer> stillAlive, Party winningParty) {
        stillAlive
                .filter(p -> partyManager.getParty(p).equals(winningParty))
                .forEach(winner -> xpHandler.addXP(winner, XPConfig::win));

        LOGGER.debug("Giving winner xp to players {}", stillAlive);

        var top3 = players.toList()
                .sorted(Comparator.comparingInt(player ->
                        game.getInfo(player.getPlayerId())
                                .map(PlayerInformation::getKillCount)
                                .getOrElse(0)))
                .take(3);
        top3.forEach(p -> xpHandler.addXP(p, XPConfig::top3));
        LOGGER.debug("Giving top 3 xp to players {}", top3);
    }

    private void saveGameStats(Game game, Party winningParty) {
        var stats = new GameStatistic(
                game.getUuid(),
                game.getArena().name(),
                Instant.ofEpochMilli(game.getTimer().getStartTimeMillis()),
                Instant.now(),
                game.getParties().stream().flatMap(party -> party.getAllMembers().stream()).collect(Collectors.toSet()),
                Set.copyOf(winningParty.getAllMembers()),
                game.getDeaths(),
                HashMap.ofAll(game.getPlayerInformationMap()).mapValues(PlayerInformation::createStatistics).toJavaMap()
        );
        gamePersistence.save(stats);
        LOGGER.debug("Saved stats {}", stats);
    }
}
