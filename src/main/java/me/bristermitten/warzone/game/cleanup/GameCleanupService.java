package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;

public interface GameCleanupService {
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
    @NotNull Future<Unit> scheduleCleanup(@NotNull final Game game);

    void cleanup(@NotNull final Game game);
}
