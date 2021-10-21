package me.bristermitten.warzone.game.spawning.bus;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.vavr.collection.Stream;
import me.bristermitten.warzone.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class BattleBusEquipmentUpdateCancelListener extends PacketAdapter implements EventListener {
    private final ProtocolManager protocolManager;

    @Inject
    public BattleBusEquipmentUpdateCancelListener(Plugin plugin, ProtocolManager protocolManager) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT);
        this.protocolManager = protocolManager;
    }

    @Override
    public void register(Plugin plugin) {
        protocolManager.addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent packetEvent) {
        final Integer id = packetEvent.getPacket().getIntegers().read(0);
        var entityOpt = Stream.ofAll(Bukkit.getWorlds())
                .flatMap(World::getEntities)
                .filter(entity -> entity.getEntityId() == id)
                .headOption();
        if (entityOpt.isEmpty()) {
            return;
        }
        var entity = entityOpt.get();
        if (!entity.hasMetadata(BattleBusFactory.META_KEY)) {
            return;
        }
        var target = packetEvent.getPlayer();
        packetEvent.setCancelled(entity.getPassengers().contains(target)); // if they are riding the bus, they shouldn't see the equipment
    }
}

