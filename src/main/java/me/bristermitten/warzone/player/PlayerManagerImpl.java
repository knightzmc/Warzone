package me.bristermitten.warzone.player;

import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class PlayerManagerImpl implements PlayerManager {
    private final PlayerStorage playerStorage;
    private final PlayerStates playerStates;
    private final Schedule schedule;

    @Inject
    public PlayerManagerImpl(PlayerStorage playerStorage, PlayerStates playerStates, Schedule schedule) {
        this.playerStorage = playerStorage;
        this.playerStates = playerStates;
        this.schedule = schedule;
    }

    public void setState(WarzonePlayer player, Function<PlayerStates, PlayerState> newStateFunction) {
        Runnable run = () -> {
            var newState = newStateFunction.apply(playerStates);
            var event = new PlayerStateChangeEvent(player.getCurrentState(), newState, player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            player.setCurrentState(newState);
        };
        if (Bukkit.isPrimaryThread()) {
            run.run();
        } else {
            schedule.runSync(run).get();
        }
    }

    public void loadPlayer(@NotNull UUID id, @NotNull Consumer<WarzonePlayer> onSuccess) {
        playerStorage.load(id).onSuccess(onSuccess);
    }

    public @NotNull Future<@NotNull WarzonePlayer> loadPlayer(@NotNull UUID id) {
        return playerStorage.load(id);
    }

    public @NotNull Option<WarzonePlayer> lookupPlayer(@NotNull UUID uuid) {
        return Option.of(playerStorage.fetch(uuid))
                .onEmpty(() -> playerStorage.load(uuid));
    }
}
