package me.bristermitten.warzone.player;

import me.bristermitten.warzone.player.state.InLobbyState;
import me.bristermitten.warzone.player.state.OfflineState;
import me.bristermitten.warzone.util.Sync;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerJoinQuitListener implements Listener {
    private final @NotNull Plugin plugin;
    private final InLobbyState inLobbyState;

    private final OfflineState offlineState;
    private final PlayerManager playerManager;

    @Inject
    PlayerJoinQuitListener(@NotNull Plugin plugin, InLobbyState inLobbyState, OfflineState offlineState, PlayerManager playerManager) {
        this.plugin = plugin;
        this.inLobbyState = inLobbyState;
        this.offlineState = offlineState;
        this.playerManager = playerManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> Sync.run(() -> playerManager.setState(player, inLobbyState), plugin));
    }

    @EventHandler
    public void onLeave(@NotNull PlayerQuitEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> Sync.run(() -> playerManager.setState(player, offlineState), plugin));
    }
}