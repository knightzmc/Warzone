package me.bristermitten.warzone.game;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.state.*;
import me.bristermitten.warzone.game.statistic.GamePersistence;
import me.bristermitten.warzone.game.statistic.GameStatistic;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.game.world.GameWorldUpdateTask;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Schedule;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class GameManagerImpl implements GameManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameManagerImpl.class);
    private final Set<Game> games = new HashSet<>();
    private final GameStates states;
    private final PlayerManager playerManager;
    private final PartyManager partyManager;
    private final ArenaManager arenaManager;
    private final GameWorldUpdateTask gameWorldUpdateTask;
    private final LangService langService;
    private final GamePersistence gamePersistence;
    private final XPHandler xpHandler;
    private final Schedule schedule;
    private final GameFactory gameFactory;

    @Inject
    public GameManagerImpl(GameStates states,
                           PlayerManager playerManager,
                           PartyManager partyManager,
                           ArenaManager arenaManager,
                           GameWorldUpdateTask gameWorldUpdateTask,
                           LangService langService,
                           XPHandler xpHandler,
                           GamePersistence gamePersistence, Schedule schedule, GameFactory gameFactory) {
        this.states = states;
        this.playerManager = playerManager;
        this.partyManager = partyManager;
        this.arenaManager = arenaManager;
        this.gameWorldUpdateTask = gameWorldUpdateTask;
        this.langService = langService;
        this.gamePersistence = gamePersistence;
        this.xpHandler = xpHandler;
        this.schedule = schedule;
        this.gameFactory = gameFactory;
    }

    public @NotNull Set<Game> getGames() {
        return games;
    }

    @Override
    public Game createNewGame(Arena arena, PartySize acceptedSize) {
        arenaManager.use(arena);
        var game = gameFactory.createGame(arena, new HashSet<>(), acceptedSize);
        game.getPreGameLobbyTimer().addCompletionHook(() ->
                setState(game, GameStates::inProgressState));
        games.add(game);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Created new game in arena {} with size {}", arena.name(), acceptedSize);
        }

        gameWorldUpdateTask.start();
        /* TODO i'm not really sure if this belong here. iterating an empty set is so cheap
         *   that it could just be started when the plugin loads up. but this seems like a kinda logical place to put it, for now */

        return game;
    }

    @Override
    public Future<Unit> leave(Game game, OfflinePlayer player, boolean includeParty) {
        if (!gameContains(game, player.getUniqueId())) {
            throw new IllegalArgumentException("Player is not in this game");
        }
        if (game.getState() instanceof IdlingState) {
            // This shouldn't happen, just fail silently
            return Future.successful(Unit.INSTANCE);
        }
        var party = partyManager.getParty(player.getUniqueId());
        if (includeParty || party.getSize() == PartySize.SINGLES) {
            return Future.sequence(
                            List.ofAll(party.getAllMembers())
                                    .map(playerManager::loadPlayer))
                    .map(players -> {
                        players.forEach(warzonePlayer ->
                                playerManager.setState(warzonePlayer, PlayerStates::inLobbyState));
                        game.getParties().remove(party);
                        return Unit.INSTANCE;
                    }).flatMap(unit -> checkForWinner(game));
        }
        return playerManager.loadPlayer(player.getUniqueId())
                .map(warzonePlayer -> {
                    playerManager.setState(warzonePlayer, PlayerStates::inLobbyState);
                    partyManager.leave(party, player);
                    return Unit.INSTANCE;
                }).flatMap(unit -> checkForWinner(game));
    }

    @Override
    public void addToGame(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // this should never really happen so i'm not too concerned about a user friendly error message here
        }
        if (game.getState() instanceof IdlingState) {
            // fire it up!
            setState(game, GameStates::inLobbyState);
        }

        if (game.getState() instanceof InLobbyState) {
            game.getParties().add(party);
            party.getAllMembers().forEach(uuid -> {
                game.getPlayerInformationMap().put(uuid, new PlayerInformation(uuid));
                playerManager.loadPlayer(uuid, player ->
                        playerManager.setState(player, PlayerStates::inPreGameLobbyState));
            });

            if (game.isFull()) {
                setState(game, GameStates::inProgressState);
            }
        }
        // Otherwise people can't join
    }

    @Override
    public @NotNull Option<Game> getGameContaining(UUID uuid) {
        return List.ofAll(getGames())
                .filter(game -> gameContains(game, uuid))
                .headOption();
    }

    @Override
    public @NotNull Option<Game> getGameContaining(Party party) {
        return List.ofAll(getGames())
                .filter(game -> game.getParties().contains(party))
                .headOption();
    }

    /**
     * Get all players in a given Game
     * This method relies on a {@link WarzonePlayer} instance existing in the {@link PlayerManager} stored in this class,
     * as it will only call {@link PlayerManager#lookupPlayer(UUID)} to get a result immediately
     */
    @Override
    public List<WarzonePlayer> getPlayers(Game game) {
        return io.vavr.collection.List.ofAll(game.getParties())
                .flatMap(Party::getAllMembers)
                .map(playerManager::lookupPlayer)
                .filter(Option::isDefined)
                .map(Option::get);
    }

    @Override
    public Future<Seq<WarzonePlayer>> getAllPlayers(Game game) {
        return io.vavr.collection.List.ofAll(game.getParties())
                .flatMap(Party::getAllMembers)
                .map(playerManager::loadPlayer)
                .transform(Future::sequence);
    }

    @Override
    public boolean gameContains(Game game, UUID uuid) {
        return game.getPartiesInGame().toStream().filter(party -> party.getAllMembers().contains(uuid)).nonEmpty();
    }

    @Override
    public Future<Unit> checkForWinner(Game game) {
        return getAllPlayers(game).flatMap(schedule.runSync(players -> {
            var stillAlive = players.filter(player -> player.getCurrentState() instanceof AliveState);
            var remainingParties = stillAlive.groupBy(partyManager::getParty);

            if (remainingParties.isEmpty()) {
                LOGGER.debug("Cleaning up game {} in arena {} because remainingParties is empty", game.getUuid(), game.getArena().name());
                //pack it up boys
                cleanup(game, players);
                return;
            }
            if (remainingParties.size() != 1) {
                LOGGER.debug("Not doing anything, because remainingParties.size() != 1 ({})", remainingParties);
                // The game isn't over yet!
                return;
            }
            game.getGameBorder().pause();
            game.getGameBossBar().setPaused(true);

            var winningParty = remainingParties.keySet().head();
            var stats = new GameStatistic(
                    game.getUuid(),
                    game.getArena().name(),
                    Instant.ofEpochMilli(game.getTimer().getStartTimeMillis()),
                    Instant.now(),
                    game.getParties().stream().flatMap(party -> party.getAllMembers().stream()).collect(Collectors.toSet()),
                    Set.copyOf(winningParty.getAllMembers()),
                    game.getDeaths(),
                    HashMap.ofAll(game.getPlayerInformationMap())
                            .mapValues(PlayerInformation::createStatistics).toJavaMap()
            );
            gamePersistence.save(stats);
            LOGGER.debug("Saved stats {}", stats);

            stillAlive
                    .filter(p -> partyManager.getParty(p).equals(winningParty))
                    .forEach(winner -> xpHandler.addXP(winner, XPConfig::win));
            LOGGER.debug("Giving winner xp to players {}", stillAlive);

            var top3 = players.sorted(Comparator.comparingInt(player ->
                            game.getInfo(player.getPlayerId())
                                    .map(PlayerInformation::getKillCount)
                                    .getOrElse(0)))
                    .take(3);
            top3.forEach(p -> xpHandler.addXP(p, XPConfig::top3));
            LOGGER.debug("Giving top 3 xp to players {}", top3);

            winningParty.getAllMembers().forEach(winnerId -> {
                var player = Bukkit.getPlayer(winnerId);
                Objects.requireNonNull(player); // if they're offline they should've been removed from the party
                langService.send(player, config -> config.gameLang().winner());
            });
            players.forEach(warzonePlayer ->
                    warzonePlayer.getPlayer().peek(player ->
                            langService.send(player,
                                    config -> config.gameLang().winnerBroadcast(),
                                    Map.of("{winner}",
                                            Objects.requireNonNull(Bukkit.getPlayer(winningParty.getOwner())).getName()))));
            cleanup(game, players);

        }));
    }

    // The players parameter here allows to avoid an extra getAllPlayers call
    private void cleanup(Game game, Seq<WarzonePlayer> players) {
        LOGGER.debug("Cleaning up game {} (remaining players = {})", game, players);
        players.forEach(warzonePlayer -> playerManager.setState(warzonePlayer, PlayerStates::inLobbyState));
        setState(game, GameStates::idlingState);
        game.getGameBorder().remove();

        arenaManager.free(game.getArena());
        games.remove(game);
    }


    @Override
    public void setState(Game game, Function<GameStates, GameState> stateFunction) {
        final var state = stateFunction.apply(states);
        game.setCurrentState(state);
        LOGGER.debug("Set state of game {} to {}", game, state);
    }
}
