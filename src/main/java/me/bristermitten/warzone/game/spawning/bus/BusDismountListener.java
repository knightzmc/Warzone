package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.game.spawning.ElytraPlayerSpawner;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.inject.Inject;

public class BusDismountListener implements EventListener {
    private final ProtocolWrapper protocolWrapper;
    private final PartyManager partyManager;

    @Inject
    public BusDismountListener(ProtocolWrapper protocolWrapper, PartyManager partyManager) {
        this.protocolWrapper = protocolWrapper;
        this.partyManager = partyManager;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!event.getDismounted().hasMetadata(BattleBusFactory.META_KEY)) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        player.setInvisible(false);

        protocolWrapper.makePartyMembersGlow(partyManager.getParty(player));
        ElytraPlayerSpawner.giveElytra(player);
    }
}
