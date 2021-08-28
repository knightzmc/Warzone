package me.bristermitten.warzone.game.gulag;


import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.death.GameDeathHandler;
import me.bristermitten.warzone.game.spawning.PlayerSpawner;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Processes a death when a player is in the gulag
 */
public class GulagDeathHandler implements GameDeathHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GulagDeathHandler.class);
    private final PlayerManager playerManager;
    private final GameManager gameManager;
    private final PlayerSpawner playerSpawner;
    private final GulagManager gulagManager;
    private final Schedule schedule;

    @Inject
    public GulagDeathHandler(PlayerManager playerManager, GameManager gameManager, PlayerSpawner playerSpawner, GulagManager gulagManager, Schedule schedule) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
        this.playerSpawner = playerSpawner;
        this.gulagManager = gulagManager;
        this.schedule = schedule;
    }

    /*
     * If a player can be sent to the gulag, send them
     * If a player dies in the gulag, remove them from the game
     * Otherwise, spawn them back in
     */
    @Override
    public void onDeath(PlayerDeathEvent event) {
        event.setCancelled(true);
        playerManager.loadPlayer(event.getEntity().getUniqueId(), warzonePlayer -> {
            var gameOpt = gameManager.getGameContaining(event.getEntity().getUniqueId());
            if (gulagManager.gulagIsAvailableFor(warzonePlayer) && gameOpt.isDefined()) {
                gulagManager.addToGulag(gameOpt.get().getGulag(), warzonePlayer);
                return;
            }

            playerManager.setState(warzonePlayer, PlayerStates::spectatingState);
        });

        var killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        // Respawn the killer
        gameManager.getGameContaining(killer.getUniqueId())
                .peek(game ->
                        playerManager.loadPlayer(killer.getUniqueId()).flatMap(schedule.runSync(killerW -> {
                            playerManager.setState(killerW, PlayerStates::aliveState);
                            playerSpawner.spawn(game, killerW);
                        })))
                .onEmpty(() -> LOGGER.warn("Player {} killed {} in a gulag even though they aren't in a game", killer.getName(), event.getEntity().getName()));
    }

}
