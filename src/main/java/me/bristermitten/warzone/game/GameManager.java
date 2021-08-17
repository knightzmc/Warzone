package me.bristermitten.warzone.game;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.gulag.GulagManager;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.game.InGulagState;
import org.jetbrains.annotations.Contract;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class GameManager {
    private final Set<Game> games = new HashSet<>();

    private final GameStates states;
    private final PlayerStates playerStates;
    private final PlayerManager playerManager;
    private final ArenaManager arenaManager;
    private final GulagManager gulagManager;


    @Inject
    public GameManager(GameStates states, PlayerStates playerStates, PlayerManager playerManager, ArenaManager arenaManager, GulagManager gulagManager) {
        this.states = states;
        this.playerStates = playerStates;
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
        this.gulagManager = gulagManager;
    }

    public Set<Game> getGames() {
        return games;
    }

    /**
     * Create a new Game in the Idling State and add it to the games set
     * This also marks the Arena as "in use" to the supplied {@link ArenaManager} and as such will throw any exceptions
     * that {@link ArenaManager#use(Arena)} throws
     *
     * @param arena        The arena to create the game under
     * @param acceptedSize The allowed party size in the game
     * @return the new game
     * @throws IllegalArgumentException if arena is already in use
     */
    @Contract("_, _ -> new")
    public Game createNewGame(Arena arena, PartySize acceptedSize) {
        arenaManager.use(arena);
        Game game = new Game(arena, new HashSet<>(), acceptedSize);
        games.add(game);
        return game;
    }

    public void addToGame(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // this should never really happen so i'm not too concerned about a user friendly error message here
        }
        if (game.getState() instanceof IdlingState) {
            // fire it up!
            game.setCurrentState(states.inLobbyStateProvider().get());
        }

        if (game.getState() instanceof InLobbyState) {
            game.getParties().add(party);
            party.getAllMembers().forEach(uuid -> game.getPlayerInformationMap().put(uuid, new PlayerInformation(uuid)));
            party.setLocked(true); // TODO unlock
            party.getAllMembers().forEach(uuid ->
                    playerManager.loadPlayer(uuid, player ->
                            playerManager.setState(player, playerStates.inPreGameLobbyState())));

            if (game.isFull()) {
                game.setCurrentState(states.inProgressStateProvider().get());
            }
        }
        // Otherwise people can't join
    }

    public Option<Game> getGameContaining(UUID uuid) {
        return List.ofAll(getGames())
                .filter(game -> gameContains(game, uuid))
                .headOption();
    }

    /**
     * Get all players in a given Game
     * This method relies on a {@link WarzonePlayer} instance existing in the {@link PlayerManager} stored in this class,
     * as it will only call {@link PlayerManager#lookupPlayer(UUID)} to get a result immediately
     */
    public List<WarzonePlayer> getPlayers(Game game) {
        return io.vavr.collection.List.ofAll(game.getParties())
                .flatMap(Party::getAllMembers)
                .map(playerManager::lookupPlayer)
                .filter(Option::isDefined)
                .map(Option::get);
    }

    public Future<Seq<WarzonePlayer>> getAllPlayers(Game game) {
        return io.vavr.collection.List.ofAll(game.getParties())
                .flatMap(Party::getAllMembers)
                .map(playerManager::loadPlayer)
                .transform(Future::sequence);
    }

    public boolean gameContains(Game game, UUID uuid) {
        return game.getPartiesInGame().stream().anyMatch(party -> party.getAllMembers().contains(uuid));
    }

    public void handleDeath(Game game, UUID died) {
        if (!gameContains(game, died)) {
            throw new IllegalArgumentException("Player is not in this game! something has gone very wrong");
        }
        var playerInfo = game.getInfo(died)
                .getOrElseThrow(() -> new IllegalStateException("Something has also gone very wrong as the player has no PlayerInformation"));

        playerManager.loadPlayer(died, player -> {
            if (playerInfo.getDeathCount() > game.getArena().gameConfiguration().maxGulagEntries()
                || player.getCurrentState() instanceof InGulagState
                || !gulagManager.gulagIsAvailable(game.getGulag())) {
                // they're out
                playerManager.setState(player, PlayerStates::spectatingState);
            } else {
                gulagManager.addToGulag(game.getGulag(), player);
            }
        });
    }
}
