package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;

import javax.inject.Inject;

public class SpectatingStateInteractionListener implements EventListener {
    private final PlayerManager playerManager;

    @Inject
    SpectatingStateInteractionListener(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) {
            return;
        }
        playerManager.lookupPlayer(damager.getUniqueId())
                .peek(damagerPlayer -> {
                    if (damagerPlayer.getCurrentState() instanceof SpectatingState) {
                        event.setCancelled(true);
                    }
                });

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        playerManager.lookupPlayer(event.getPlayer().getUniqueId())
                .peek(damagerPlayer -> {
                    if (damagerPlayer.getCurrentState() instanceof SpectatingState) {
                        event.setCancelled(true);
                    }
                });
    }

    @EventHandler
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        playerManager.lookupPlayer(player.getUniqueId())
                .peek(damagerPlayer -> {
                    if (damagerPlayer.getCurrentState() instanceof SpectatingState) {
                        event.setCancelled(true);
                    }
                });
    }
}
