package me.bristermitten.warzone.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.bristermitten.warzone.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;

public class ProtocolWrapper {
    private final ProtocolManager protocolManager;

    @Inject
    public ProtocolWrapper(ProtocolManager protocolManager) {
        this.protocolManager = protocolManager;
    }

    public void makePlayerGlowing(@NotNull Player target, @NotNull Player viewer) {
        sendMetadataPacket(target, viewer, mask -> (byte) (mask | 0x40)); // add glowing
    }

    public void makePartyMembersGlow(Party party) {
        party.getAllMembers().forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                return;
            }
            party.getAllMembers().forEach(otherUUID -> {
                if (otherUUID.equals(uuid)) {
                    return;
                }
                var otherPlayer = Bukkit.getPlayer(otherUUID);
                if (otherPlayer == null) {
                    return;
                }
                makePlayerGlowing(otherPlayer, player);
            });
        });
    }

    private void sendMetadataPacket(@NotNull Player target, @NotNull Player viewer, @NotNull ByteUnaryOperator maskOp) {
        if (target == viewer) {
            return;
        }
        var packet = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, target.getEntityId());

        WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(target);
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);

        byte mask = watcher.getByte(0);
        mask = maskOp.applyAsByte(mask);


        watcher.setEntity(target); //Set the new data watcher's target
        watcher.setObject(0, serializer, mask); //Set status to glowing, found on protocol page
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());

        try {
            protocolManager.sendServerPacket(viewer, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public void undoPlayerGlowing(@NotNull Player target, @NotNull Player viewer) {
        this.sendMetadataPacket(target, viewer, mask -> (byte) (mask & ~0x40)); // remove glowing effect
    }

    @FunctionalInterface
    interface ByteUnaryOperator {
        byte applyAsByte(byte operand);
    }
}
