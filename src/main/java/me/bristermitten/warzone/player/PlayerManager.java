package me.bristermitten.warzone.player;

import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.util.Sync;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerManager {
    private final Plugin plugin;
    private final PlayerStorage playerStorage;

    @Inject
    public PlayerManager(Plugin plugin, PlayerStorage playerStorage) {
        this.plugin = plugin;
        this.playerStorage = playerStorage;
    }

    public void setState(WarzonePlayer player, PlayerState newState) {
        Sync.run(() -> {
            var event = new PlayerStateChangeEvent(player.getCurrentState(), newState, player);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }
            player.setCurrentState(newState);
        }, plugin);
    }

    public void loadPlayer(@NotNull UUID id, Consumer<WarzonePlayer> onSuccess) {
        playerStorage.load(id).onSuccess(onSuccess);
    }
}
