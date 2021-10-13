package me.bristermitten.warzone.player;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class PlayerJoinQuitListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerJoinQuitListener.class);
    private final PlayerManager playerManager;

    @Inject
    PlayerJoinQuitListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        LOGGER.debug("Player {} joined the server ({})", event.getPlayer(), event);
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> playerManager.setState(player, PlayerStates::inLobbyState));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> playerManager.setState(player, PlayerStates::offlineState));
    }
}
