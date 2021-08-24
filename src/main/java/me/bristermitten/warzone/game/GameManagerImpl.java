package me.bristermitten.warzone.game;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.gulag.GulagManager;
import me.bristermitten.warzone.game.state.*;
import me.bristermitten.warzone.game.statistic.GamePersistence;
import me.bristermitten.warzone.game.statistic.GameStatistic;
import me.bristermitten.warzone.game.statistic.PlayerDeath;
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
import me.bristermitten.warzone.player.state.game.InGulagState;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.Schedule;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Singleton
public class GameManagerImpl implements GameManager {
    private final Set<Game> games = new HashSet<>();

    private final GameStates states;
    private final PlayerManager playerManager;
    private final PartyManager partyManager;
    private final ArenaManager arenaManager;
    private final GulagManager gulagManager;
    private final GameWorldUpdateTask gameWorldUpdateTask;
    private final LangService langService;
    private final GamePersistence gamePersistence;
    private final XPHandler xpHandler;
    private final Schedule schedule;


    @Inject
    public GameManagerImpl(GameStates states,
                           PlayerManager playerManager,
                           PartyManager partyManager,
                           ArenaManager arenaManager,
                           GulagManager gulagManager,
                           GameWorldUpdateTask gameWorldUpdateTask,
                           LangService langService,
                           XPHandler xpHandler,
                           GamePersistence gamePersistence, Schedule schedule) {
        this.states = states;
        this.playerManager = playerManager;
        this.partyManager = partyManager;
        this.arenaManager = arenaManager;
        this.gulagManager = gulagManager;
        this.gameWorldUpdateTask = gameWorldUpdateTask;
        this.langService = langService;
        this.gamePersistence = gamePersistence;
        this.xpHandler = xpHandler;
        this.schedule = schedule;
    }

    public @NotNull Set<Game> getGames() {
        return games;
    }

    @Override
    public Game createNewGame(Arena arena, PartySize acceptedSize) {
        arenaManager.use(arena);
        Game game = new Game(arena, new HashSet<>(), acceptedSize);
        games.add(game);

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
            return Future.sequence(List.ofAll(party.getAllMembers()).map(playerManager::loadPlayer))
                    .map(players -> {
                        players.forEach(warzonePlayer ->
                                playerManager.setState(warzonePlayer, PlayerStates::inLobbyState));
                        game.getParties().remove(party);
                        cleanup(game);
                        return Unit.INSTANCE;
                    });
        } else {
            return playerManager.loadPlayer(player.getUniqueId())
                    .map(warzonePlayer -> {
                        playerManager.setState(warzonePlayer, PlayerStates::inLobbyState);
                        partyManager.leave(party, player);
                        cleanup(game);
                        return Unit.INSTANCE;
                    });
        }
    }

    @Override
    public void addToGame(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // this should never really happen so i'm not too concerned about a user friendly error message here
        }
        if (game.getState() instanceof IdlingState) {
            // fire it up!
            setState(game, GameStates::inLobbyStateProvider);
        }

        if (game.getState() instanceof InLobbyState) {
            game.getParties().add(party);
            party.getAllMembers().forEach(uuid -> {
                game.getPlayerInformationMap().put(uuid, new PlayerInformation(uuid));
                playerManager.loadPlayer(uuid, player ->
                        playerManager.setState(player, PlayerStates::inPreGameLobbyState));
            });

            if (game.isFull()) {
                setState(game, GameStates::inProgressStateProvider);
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
        return game.getPartiesInGame().stream().anyMatch(party -> party.getAllMembers().contains(uuid));
    }

    @Override
    public void handleDeath(Game game, UUID died, PlayerDeathEvent event) {
        if (!gameContains(game, died)) {
            throw new IllegalArgumentException("Player is not in this game! something has gone very wrong");
        }
        if (!(game.getState() instanceof InProgressState)) {
            // This probably shouldn't happen, i guess we'll ignore it
            return;
        }
        var playerInfo = game.getInfo(died)
                .getOrElseThrow(() -> new IllegalStateException("Something has also gone very wrong as the player has no PlayerInformation"));

        game.getDeaths().add(new PlayerDeath(
                died,
                Option.of(Bukkit.getPlayer(died)).flatMap(p -> Option.of(p.getKiller())).map(Player::getUniqueId).getOrNull(),
                Instant.now(),
                Option.of(event.getEntity().getLastDamageCause())
                        .map(EntityDamageEvent::getCause)
                        .map(cause -> switch (cause) {
                            case FALL -> PlayerDeath.DeathCause.FALL_DAMAGE;
                            case CUSTOM -> PlayerDeath.DeathCause.BORDER;
                            default -> PlayerDeath.DeathCause.OTHER;
                        }).getOrElse(PlayerDeath.DeathCause.UNKNOWN)
        ));

        playerManager.loadPlayer(died, player -> {
            if (playerInfo.getDeathCount() > game.getArena().gameConfig().maxGulagEntries()
                || player.getCurrentState() instanceof InGulagState
                || !gulagManager.gulagIsAvailable(game.getGulag())) {
                // they're out
                playerManager.setState(player, PlayerStates::spectatingState);
                checkForWinner(game);
            } else {
                gulagManager.addToGulag(game.getGulag(), player);
            }
        });
    }


    private void checkForWinner(Game game) {
        getAllPlayers(game).flatMap(schedule.runSync(players -> {
            var stillAlive = players.filter(player -> player.getCurrentState() instanceof AliveState);
            var remainingParties = stillAlive.groupBy(partyManager::getParty);

            if (remainingParties.isEmpty()) {
                //pack it up boys
                cleanup(game);
                return;
            }
            if (remainingParties.keySet().size() != 1) {
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
                    Instant.ofEpochMilli(System.currentTimeMillis()),
                    game.getParties().stream().flatMap(party -> party.getAllMembers().stream()).collect(Collectors.toSet()),
                    Set.copyOf(winningParty.getAllMembers()),
                    game.getDeaths(),
                    HashMap.ofAll(game.getPlayerInformationMap())
                            .mapValues(PlayerInformation::createStatistics).toJavaMap()
            );
            gamePersistence.save(stats);

            stillAlive
                    .filter(p -> partyManager.getParty(p).equals(winningParty))
                    .forEach(winner -> xpHandler.addXP(winner, XPConfig::win));

            winningParty.getAllMembers().forEach(winnerId -> {
                var player = Bukkit.getPlayer(winnerId);
                Objects.requireNonNull(player); // if they're offline they should've been removed from the party
                langService.sendMessage(player, config -> config.gameLang().winner());
            });
            players.forEach(warzonePlayer -> {
                warzonePlayer.getPlayer().peek(player ->
                        langService.sendMessage(player,
                                config -> config.gameLang().winnerBroadcast(),
                                Map.of("{winner}",
                                        Objects.requireNonNull(Bukkit.getPlayer(winningParty.getOwner())).getName())));
                playerManager.setState(warzonePlayer, PlayerStates::inLobbyState);
            });
            cleanup(game);
        }));
    }

    private void cleanup(Game game) {
        setState(game, GameStates::idlingState);
        arenaManager.free(game.getArena());
        games.remove(game);
    }


    @Override
    public void setState(Game game, Function<GameStates, GameState> stateFunction) {
        game.setCurrentState(stateFunction.apply(states));
    }
}
