package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.cleanup.GameEndingService;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerStateChangeEvent;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import me.bristermitten.warzone.util.Schedule;
import net.kyori.adventure.util.Ticks;
import org.bukkit.event.EventHandler;

import javax.inject.Inject;

public class GamePlayerRemoveListener implements EventListener {
    private final GameRepository gameRepository;
    private final GameEndingService gameEndingService;
    private final Schedule schedule;

    @Inject
    GamePlayerRemoveListener(GameRepository gameRepository, GameEndingService gameEndingService, Schedule schedule) {
        this.gameRepository = gameRepository;
        this.gameEndingService = gameEndingService;
        this.schedule = schedule;
    }

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if (!(event.getNewState() instanceof SpectatingState)) {
            return;
        }
        schedule.runLater(Ticks.SINGLE_TICK_DURATION_MS, () ->
                gameRepository.getGameContaining(event.getSubject())
                        .peek(gameEndingService::endIfShould));
        /*
         We run this a tick later because the event is called BEFORE the player's state has changed
         when checking for a winner, the game checks how many players are not in the SpectatingState (i.e still alive)
         and so if we call it immediately, it won't count this player as having died
         In the future this could perhaps be fixed by making StateChangeEvent not cancellable (and so the event
         could be called after the state change actually happens), but for now this is the best future-proof way.
        */

    }
}
