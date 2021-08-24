package me.bristermitten.warzone.player.state.game;

import com.google.inject.Inject;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.inject.Provider;

public class SpectatingStateHotbarItemListener implements EventListener {
    private final Provider<GameConfig> gameConfigProvider;
    private final PlayerManager playerManager;
    private final Schedule schedule;

    @Inject
    public SpectatingStateHotbarItemListener(Provider<GameConfig> gameConfigProvider, PlayerManager playerManager, Schedule schedule) {
        this.gameConfigProvider = gameConfigProvider;
        this.playerManager = playerManager;
        this.schedule = schedule;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
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
        event.setCancelled(true);

        playerManager.loadPlayer(event.getPlayer().getUniqueId())
                .filter(warzonePlayer -> warzonePlayer.getCurrentState() instanceof SpectatingState)
                .onSuccess(warzonePlayer -> {
                    var command = switch (clickedItem.action()) {
                        case LEAVE_GAME -> "warzone leave";
                        case REQUEUE -> "warzone requeue";
                    };
                    schedule.runSync(() -> event.getPlayer().performCommand(command));
                });
    }
}
