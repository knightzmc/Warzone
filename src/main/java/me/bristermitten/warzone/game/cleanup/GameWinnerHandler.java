package me.bristermitten.warzone.game.cleanup;

import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.util.Unit;
import org.jetbrains.annotations.NotNull;

public interface GameWinnerHandler {
    @NotNull Option<Party> getWinner(@NotNull final Game game);

    @NotNull Future<Unit> giveWinnerRewards(@NotNull final Game game, @NotNull final Party winners);
}
