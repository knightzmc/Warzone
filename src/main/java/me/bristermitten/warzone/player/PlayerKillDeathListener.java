package me.bristermitten.warzone.player;

import me.bristermitten.warzone.game.death.GameDeathHandler;
import me.bristermitten.warzone.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Set;

public class PlayerKillDeathListener implements EventListener {
    private final Set<GameDeathHandler> gameDeathHandlers;

    @Inject
    PlayerKillDeathListener(Set<GameDeathHandler> gameDeathHandlers) {
        this.gameDeathHandlers = gameDeathHandlers;
    }

    @EventHandler
    public void onKill(@NotNull PlayerDeathEvent event) {
        event.setCancelled(true);
        for (GameDeathHandler gameDeathHandler : gameDeathHandlers) {
            gameDeathHandler.onDeath(event);
        }
    }
}
