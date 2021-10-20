package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;

public interface GameCleanupService {
    @NotNull Future<Unit> scheduleCleanup(@NotNull final Game game);

    void cleanup(@NotNull final Game game);
}
