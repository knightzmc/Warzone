package me.bristermitten.warzone.game;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.gulag.GulagManager;
import me.bristermitten.warzone.game.state.*;
import me.bristermitten.warzone.game.world.GameWorldUpdateTask;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.game.InGulagState;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

@Singleton
public class GameManagerImpl implements GameManager {
    private final Set<Game> games = new HashSet<>();

    private final GameStates states;
    private final PlayerManager playerManager;
    private final PartyManager partyManager;
    private final ArenaManager arenaManager;
    private final GulagManager gulagManager;
    private final GameWorldUpdateTask gameWorldUpdateTask;


    @Inject
    public GameManagerImpl(GameStates states,
                           PlayerManager playerManager,
                           PartyManager partyManager, ArenaManager arenaManager,
                           GulagManager gulagManager,
                           GameWorldUpdateTask gameWorldUpdateTask) {
        this.states = states;
        this.playerManager = playerManager;
        this.partyManager = partyManager;
        this.arenaManager = arenaManager;
        this.gulagManager = gulagManager;
        this.gameWorldUpdateTask = gameWorldUpdateTask;
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
    public void leave(Game game, OfflinePlayer player, boolean includeParty) {
        if (!gameContains(game, player.getUniqueId())) {
            throw new IllegalArgumentException("Player is not in this game");
        }
        if (game.getState() instanceof IdlingState) {
            // This shouldn't happen, just fail silently
            return;
        }
        var party = partyManager.getParty(player.getUniqueId());
        if (includeParty || party.getSize() == PartySize.SINGLES) {
            Future.sequence(
                    List.ofAll(party.getAllMembers())
                            .map(playerManager::loadPlayer))
                    .onSuccess(players -> {
                        players.forEach(warzonePlayer ->
                                playerManager.setState(warzonePlayer, PlayerStates::inLobbyState));
                        game.getParties().remove(party);
                    });
        } else {
            playerManager.loadPlayer(player.getUniqueId())
                    .onSuccess(warzonePlayer -> {
                        playerManager.setState(warzonePlayer, PlayerStates::inLobbyState);
                        partyManager.leave(party, player);
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
    public void handleDeath(Game game, UUID died) {
        if (!gameContains(game, died)) {
            throw new IllegalArgumentException("Player is not in this game! something has gone very wrong");
        }
        if (!(game.getState() instanceof InProgressState)) {
            // This probably shouldn't happen, i guess we'll ignore it
            return;
        }
        var playerInfo = game.getInfo(died)
                .getOrElseThrow(() -> new IllegalStateException("Something has also gone very wrong as the player has no PlayerInformation"));

        playerManager.loadPlayer(died, player -> {
            if (playerInfo.getDeathCount() > game.getArena().gameConfig().maxGulagEntries()
                || player.getCurrentState() instanceof InGulagState
                || !gulagManager.gulagIsAvailable(game.getGulag())) {
                playerInfo.setAlive(false);
                // they're out
                playerManager.setState(player, PlayerStates::spectatingState);
            } else {
                gulagManager.addToGulag(game.getGulag(), player);
            }
        });
    }


    @Override
    public void setState(Game game, Function<GameStates, GameState> stateFunction) {
        game.setCurrentState(stateFunction.apply(states));
    }
}
