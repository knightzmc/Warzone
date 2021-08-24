package me.bristermitten.warzone.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

public class ProtocolWrapper {
    private final ProtocolManager protocolManager;

    @Inject
    public ProtocolWrapper(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void makePlayerGlowing(Player target, Player viewer) {

        var packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, target.getEntityId());

        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(target);
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);

        byte mask = watcher.getByte(0);
        mask |= 0x40;

        watcher.setEntity(target); //Set the new data watcher's target
        watcher.setObject(0, serializer, mask); //Set status to glowing, found on protocol page
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(viewer, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
