package me.bristermitten.warzone.player.state.game;

import com.google.inject.Inject;
import io.vavr.control.Option;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Provider;

public class SpectatingStateHotbarItemListener implements EventListener {
    private final Provider<GameConfig> gameConfigProvider;
    private final PlayerManager playerManager;

    @Inject
    public SpectatingStateHotbarItemListener(Provider<GameConfig> gameConfigProvider, PlayerManager playerManager) {
        this.gameConfigProvider = gameConfigProvider;
        this.playerManager = playerManager;
    }

    /**
     * Cancels ALL interactions when a player is spectating
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClick(PlayerInteractEvent event) {
        final Option<WarzonePlayer> warzonePlayers = playerManager.lookupPlayer(event.getPlayer().getUniqueId());
        if (warzonePlayers.isEmpty()) {
            return;
        }
        var warzonePlayer = warzonePlayers.get();
        if (!(warzonePlayer.getCurrentState() instanceof SpectatingState)) {
            return;
        }

        event.setCancelled(true); // cancel all interactions

        var item = event.getItem();
        if (item == null) {
            return;
        }
        var clickedItem = gameConfigProvider.get().spectatorConfig().hotbarItems()
                .get(event.getPlayer().getInventory().getHeldItemSlot());
        if (clickedItem == null) {
            return;
        }
        if (!item.equals(clickedItem.item())) {
            return;
        }
        var command = switch (clickedItem.action()) {
            case LEAVE_GAME -> "warzone leave";
            case REQUEUE -> "warzone requeue";
        };
        event.getPlayer().performCommand(command);
    }
}
