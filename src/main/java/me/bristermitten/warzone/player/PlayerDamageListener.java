package me.bristermitten.warzone.player;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.player.state.game.InGameState;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import javax.inject.Inject;

public class PlayerDamageListener implements EventListener {
    private final PlayerManager playerManager;

    @Inject
    PlayerDamageListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        playerManager.lookupPlayer(player.getUniqueId())
                .peek(warzonePlayer -> {
                    PlayerState state = warzonePlayer.getCurrentState();
                    if (!(state instanceof InGameState) || state instanceof SpectatingState) {
                        event.setCancelled(true);
                    }
                });
    }
}
