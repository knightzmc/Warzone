package me.bristermitten.warzone.game;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;

public class GamePlayerRemoveListener implements EventListener {
    private final GameManager gameManager;

    @Inject
    GamePlayerRemoveListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if (!(event.getNewState() instanceof SpectatingState)) {
            return;
        }
        gameManager.getGameContaining(event.getSubject())
                .peek(gameManager::checkForWinner);
    }
}
