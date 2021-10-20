package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;

public interface GameEndingService {
    /**
     * Forces a game to end, irrespective of whether it should or not
     * For best results this should only be called if {@link GameEndingService#shouldEnd(Game)} returns true
     */
    @NotNull Future<Unit> end(@NotNull final Game game);

    @NotNull default Future<Unit> endIfShould(@NotNull final  Game game) {
        if(shouldEnd(game)) {
            return end(game);
        }
        return Future.successful(Unit.INSTANCE);
    }

    boolean shouldEnd(@NotNull final Game game);
}
