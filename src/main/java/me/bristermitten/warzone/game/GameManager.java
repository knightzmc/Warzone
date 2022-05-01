package me.bristermitten.warzone.game;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.party.PartySize;
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
}
