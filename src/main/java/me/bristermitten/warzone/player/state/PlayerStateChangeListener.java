package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.util.Sync;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerStateChangeListener implements Listener {
    private final PlayerStorage storage;
    private final @NotNull Plugin plugin;
    private final InLobbyState inLobbyState;

    private final OfflineState offlineState;

    @Inject
    PlayerStateChangeListener(PlayerStorage storage, @NotNull Plugin plugin, InLobbyState inLobbyState, OfflineState offlineState) {
        this.storage = storage;
        this.plugin = plugin;
        this.inLobbyState = inLobbyState;
        this.offlineState = offlineState;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        storage.loadPlayer(event.getPlayer().getUniqueId(),
                player -> Sync.run(() -> player.setCurrentState(inLobbyState), plugin));
    }

    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        storage.loadPlayer(event.getPlayer().getUniqueId(),
                player -> Sync.run(() -> player.setCurrentState(offlineState), plugin));
    }
}
