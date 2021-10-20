package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;

public interface GameWinnerHandler {
    @NotNull Future<Unit> checkForWinner(@NotNull final Game game);
}
