package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.cleanup.GameWinnerHandler;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;

public class GamePlayerRemoveListener implements EventListener {
    private final GameRepository gameRepository;
    private final GameWinnerHandler gameWinnerHandler;

    @Inject
    GamePlayerRemoveListener(GameRepository gameRepository, GameWinnerHandler gameManager) {
        this.gameRepository = gameRepository;
        this.gameWinnerHandler = gameManager;
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if (!(event.getNewState() instanceof SpectatingState)) {
            return;
        }
        gameRepository.getGameContaining(event.getSubject())
                .peek(gameWinnerHandler::checkForWinner);
    }
}
