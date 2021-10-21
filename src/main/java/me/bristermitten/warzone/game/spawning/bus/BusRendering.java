package me.bristermitten.warzone.game.spawning.bus;

import com.google.inject.Provider;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.protocol.ProtocolWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Singleton
public class BusRendering {
    private final Set<BusEntry> busesToSee = new HashSet<>();
    private final ProtocolWrapper protocolWrapper;
    private final Provider<GameConfig> gameConfigProvider;

    @Inject
    public BusRendering(ProtocolWrapper protocolWrapper, Provider<GameConfig> gameConfigProvider) {
        this.protocolWrapper = protocolWrapper;
        this.gameConfigProvider = gameConfigProvider;
    }

    public void show(UUID battleBus, UUID viewer) {
        busesToSee.add(new BusEntry(battleBus, viewer));
    }

    public void renderAll() {
        busesToSee.removeIf(busEntry -> {
            var bus = busEntry.busEntityId();
            var entity = Bukkit.getEntity(bus);
            if (!(entity instanceof ArmorStand busEntity)) {
                return true;
            }
            var viewer = Bukkit.getPlayer(busEntry.viewerPlayerId());
            if (viewer == null) {
                return true;
            }
            protocolWrapper.setEntityHelmet(busEntity, viewer, gameConfigProvider.get().battleBusConfig().getModel());
            return false;
        });
    }

    private record BusEntry(UUID busEntityId, UUID viewerPlayerId) {
    }
}
