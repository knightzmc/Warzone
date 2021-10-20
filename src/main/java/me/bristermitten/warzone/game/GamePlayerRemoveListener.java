package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.cleanup.GameEndingService;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;

public class GamePlayerRemoveListener implements EventListener {
    private final GameRepository gameRepository;
    private final GameEndingService gameEndingService;

    @Inject
    GamePlayerRemoveListener(GameRepository gameRepository, GameEndingService gameEndingService) {
        this.gameRepository = gameRepository;
        this.gameEndingService = gameEndingService;
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if (!(event.getNewState() instanceof SpectatingState)) {
            return;
        }
        gameRepository.getGameContaining(event.getSubject())
                .peek(gameEndingService::endIfShould);
    }
}
