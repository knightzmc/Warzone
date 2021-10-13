package me.bristermitten.warzone.player;

import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.state.StateManager;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface PlayerManager extends StateManager<WarzonePlayer, PlayerState, PlayerStates> {
    @NotNull Option<WarzonePlayer> lookupPlayer(@NotNull UUID uuid);

    @NotNull Future<@NotNull WarzonePlayer> loadPlayer(@NotNull UUID id);


    /**
     * Equivalent to {@link PlayerManager#loadPlayerAsync(UUID, Consumer)}, except the callback will be executed on the main thread
     */
    void loadPlayer(@NotNull UUID id, @NotNull Consumer<@NotNull WarzonePlayer> onSuccess);

    void loadPlayerAsync(@NotNull UUID id, @NotNull Consumer<@NotNull WarzonePlayer> onSuccess);
}
