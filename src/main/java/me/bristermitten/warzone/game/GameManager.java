package me.bristermitten.warzone.game;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the loading and unloading of Game objects
 */
public interface GameManager {


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
    @NotNull Game createNewGame(@NotNull final Arena arena, @NotNull final PartySize acceptedSize);

    /**
     * Unloads and completely cleans up a Game. This includes:
     * <ul>
     *     <li>Removing world chests</li>
     *     <li>Clearing up world borders</li>
     *     <li>Removing any players still in the game</li>
     *     <li>Freeing up the Arena for more use</li>
     * </ul>
     * <p>
     * However, note that this is not guaranteed to complete immediately.
     * By default, this runs a 10 second timer before players are ejected to make the process look more smooth
     * The returned Future will be completed once all cleanup operations have actually finished
     *
     * @param game The game to unload
     */
    Future<Unit> unload(@NotNull final Game game);

}
