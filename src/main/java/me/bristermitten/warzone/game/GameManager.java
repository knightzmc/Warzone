package me.bristermitten.warzone.game;

import io.vavr.collection.List;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
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


    @Inject
    public GameManager(GameStates states, PlayerStates playerStates, PlayerManager playerManager, ArenaManager arenaManager) {
        this.states = states;
        this.playerStates = playerStates;
        this.playerManager = playerManager;
        this.arenaManager = arenaManager;
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
            game.getPlayers().add(party);
            party.setLocked(true); // TODO unlock
            party.getAllMembers().forEach(uuid ->
                    playerManager.loadPlayer(uuid, player ->
                            playerManager.setState(player, playerStates.inPreGameLobbyState())));
        }
        // Otherwise people can't join
    }

    public Option<Game> getGameContaining(UUID uuid) {
        return List.ofAll(getGames())
                .filter(game -> game.getPlayersInGame().stream().anyMatch(party -> party.getAllMembers().contains(uuid)))
                .headOption();
    }

}
