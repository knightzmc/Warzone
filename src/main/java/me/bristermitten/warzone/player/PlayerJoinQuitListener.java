package me.bristermitten.warzone.player;

import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.party.PartyManager;
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
    private final @NotNull Plugin plugin;
    private final GameManager gameManager;
    private final PartyManager partyManager;

    private final PlayerManager playerManager;

    @Inject
    PlayerJoinQuitListener(@NotNull Plugin plugin, GameManager gameManager, PartyManager partyManager, PlayerManager playerManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.partyManager = partyManager;
        this.playerManager = playerManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        playerManager.loadPlayer(event.getPlayer().getUniqueId(), player -> playerManager.setState(player, PlayerStates::inLobbyState));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        // Normally this would be done in OfflineState#onEnter but bukkit sucks
        gameManager.getGameContaining(event.getPlayer().getUniqueId())
                .peek(game -> gameManager.leave(game, event.getPlayer(), false))
                .onEmpty(() -> partyManager.leave(partyManager.getParty(event.getPlayer()), event.getPlayer()));

        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                player -> playerManager.setState(player, PlayerStates::offlineState));
    }
}
