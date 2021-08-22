package me.bristermitten.warzone.player.state.game;

import com.google.inject.Inject;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
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

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        var item = event.getItem();
        if (item == null) {
            return;
        }
        playerManager.loadPlayer(event.getPlayer().getUniqueId())
                .filter(warzonePlayer -> warzonePlayer.getCurrentState() instanceof SpectatingState)
                .onSuccess(warzonePlayer -> {
                    var clickedItem = gameConfigProvider.get().spectatorConfig().hotbarItems()
                            .get(event.getPlayer().getInventory().getHeldItemSlot());
                    if (clickedItem == null) {
                        return;
                    }
                    if (!item.equals(clickedItem.item())) {
                        return;
                    }
                    var command = switch (clickedItem.action()) {
                        case LEAVE -> "warzone leave";
                        case REQUEUE -> "warzone requeue";
                    };
                    event.getPlayer().performCommand(command);
                });
    }
}
