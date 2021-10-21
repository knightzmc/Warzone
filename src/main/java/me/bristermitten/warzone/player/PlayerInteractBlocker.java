package me.bristermitten.warzone.player;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.player.state.game.InGameState;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Inject;

public class PlayerInteractBlocker implements EventListener {
    private final PlayerManager playerManager;

    @Inject
    PlayerInteractBlocker(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    /**
     * Cancels interactions when a player is in game, unless they are opening a door or chest
     */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            event.setCancelled(true);
            return;
        }
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        playerManager.lookupPlayer(event.getPlayer().getUniqueId())
                .peek(warzonePlayer -> {
                    PlayerState state = warzonePlayer.getCurrentState();
                    if (state instanceof InGameState && !clickedBlock.getType().name().contains("DOOR") && !clickedBlock.getType().name().contains("CHEST")) {
                        event.setCancelled(true);
                    }
                });

    }
}
