package me.bristermitten.warzone.player;

import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class PlayerJoinQuitListener implements Listener {

    private final PlayerManager playerManager;

    @Inject
    PlayerJoinQuitListener(@NotNull Plugin plugin, PlayerManager playerManager) {
        this.playerManager = playerManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> playerManager.setState(player, PlayerStates::inLobbyState));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> playerManager.setState(player, PlayerStates::offlineState));
    }
}
