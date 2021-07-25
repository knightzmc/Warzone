package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class PlayerStateChangeListener implements Listener {
    private final PlayerStorage storage;
    private final InLobbyState inLobbyState;

    private final OfflineState offlineState;

    @Inject
    PlayerStateChangeListener(PlayerStorage storage, Plugin plugin, InLobbyState inLobbyState, OfflineState offlineState) {
        this.storage = storage;
        this.inLobbyState = inLobbyState;
        this.offlineState = offlineState;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        storage.loadPlayer(event.getPlayer().getUniqueId(),
                player -> player.setCurrentState(inLobbyState));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        storage.loadPlayer(event.getPlayer().getUniqueId(),
                player -> player.setCurrentState(offlineState));
    }
}
