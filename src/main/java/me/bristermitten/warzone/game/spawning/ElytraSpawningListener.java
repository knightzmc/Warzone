package me.bristermitten.warzone.game.spawning;

import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.game.InGameSpawningState;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.PlayerInventory;

import javax.inject.Inject;

/**
 * Stops players from removing their elytra and removes elytras when they hit the ground
 */
public class ElytraSpawningListener implements EventListener {
    private final PlayerManager playerManager;
    private final PartyManager partyManager;
    private final ProtocolWrapper protocolWrapper;

    @Inject
    ElytraSpawningListener(PlayerManager playerManager, PartyManager partyManager, ProtocolWrapper protocolWrapper) {
        this.playerManager = playerManager;
        this.partyManager = partyManager;
        this.protocolWrapper = protocolWrapper;
    }

    @EventHandler
    public void onElytraRemove(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof PlayerInventory)) {
            return;
        }
        if (!ElytraPlayerSpawner.ELYTRA_ITEM.equals(event.getCurrentItem())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onTouchGround(PlayerMoveEvent event) {
        if (!((Entity) event.getPlayer()).isOnGround()) {
            return;
        }
        if (!ElytraPlayerSpawner.ELYTRA_ITEM.equals(event.getPlayer().getInventory().getChestplate())) {
            return;
        }
        playerManager.loadPlayer(event.getPlayer().getUniqueId(), warzonePlayer -> {
            if (!(warzonePlayer.getCurrentState() instanceof InGameSpawningState)) {
                return; //we dont care
            }
            event.getPlayer().getInventory().setChestplate(null);
            partyManager.getParty(warzonePlayer)
                    .getAllMembers().forEach(partyMemberUUID -> {
                Player player = Bukkit.getPlayer(partyMemberUUID);
                if (player != null) {
                    protocolWrapper.undoPlayerGlowing(player, event.getPlayer());
                }
            });
        });
    }
}
