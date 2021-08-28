package me.bristermitten.warzone.game;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.state.StateManager;
import me.bristermitten.warzone.util.Unit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;
import java.util.UUID;

public interface GameManager extends StateManager<Game, GameState, GameStates> {
    @Unmodifiable @NotNull Set<@NotNull Game> getGames();

    boolean gameContains(Game game, UUID uuid);

    Future<Seq<WarzonePlayer>> getAllPlayers(Game game);

    /**
     * Get all players in a given Game
     * This method relies on a {@link WarzonePlayer} instance existing in the {@link PlayerManager} stored in this class,
     * as it will only call {@link PlayerManager#lookupPlayer(UUID)} to get a result immediately
     */
    List<WarzonePlayer> getPlayers(Game game);

    @NotNull Option<Game> getGameContaining(UUID uuid);

    default @NotNull Option<Game> getGameContaining(WarzonePlayer player) {
        return getGameContaining(player.getPlayerId());
    }

    @NotNull Option<Game> getGameContaining(Party party);

    void addToGame(Game game, Party party);


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
    Game createNewGame(Arena arena, PartySize acceptedSize);

    /**
     * Makes a player leave a game.
     *
     * @param game         The game to leave
     * @param player       The player to leave
     * @param includeParty Whether or not the player's party members should also leave the game
     * @return A Future that will complete once the player has left the game
     * @throws IllegalArgumentException if game does not contain playerUUID (based on {@link GameManager#gameContains(Game, UUID)}
     */
    Future<Unit> leave(Game game, OfflinePlayer player, boolean includeParty);

    Future<Unit> checkForWinner(Game game);
}
