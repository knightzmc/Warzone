package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.game.spawning.ElytraPlayerSpawner;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityDismountEvent;

import javax.inject.Inject;

public class BusDismountListener implements EventListener {
    private final ProtocolWrapper protocolWrapper;
    private final PartyManager partyManager;
    private final BusRendering busRendering;

    @Inject
    public BusDismountListener(ProtocolWrapper protocolWrapper, PartyManager partyManager, BusRendering busRendering) {
        this.protocolWrapper = protocolWrapper;
        this.partyManager = partyManager;
        this.busRendering = busRendering;
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        final Entity dismounted = event.getDismounted();
        if (!(dismounted instanceof LivingEntity dismountedEntity)) {
            return;
        }
        if (!dismountedEntity.hasMetadata(BattleBusFactory.META_KEY)) {
            return;
        }
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        busRendering.show(dismountedEntity.getUniqueId(), player.getUniqueId());
        player.setInvisible(false);

        protocolWrapper.makePartyMembersGlow(partyManager.getParty(player));

        ElytraPlayerSpawner.giveElytra(player);
    }
}
