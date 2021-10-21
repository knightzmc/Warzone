package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.data.Point;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class BattleBusFactory {
    public static final String META_KEY = "WarzoneBattleBus";

    private final Plugin plugin;

    @Inject
    public BattleBusFactory(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a new battle bus, spawning a new entity for it
     *
     * @param startPoint Where the bus starts
     * @param endPoint   Where the bus will end
     * @param speed      How many milliseconds it should take for the bus to move from its start point to its end point
     * @return a new BattleBus
     */
    public BattleBus createBus(World world, Point startPoint, Point endPoint, long speed) {
        var direction = endPoint.toLocation(world).toVector().subtract(startPoint.toLocation(world).toVector());

        var entity = world.spawn(startPoint.toLocation(world).setDirection(direction), ArmorStand.class, item -> {
            item.setInvisible(true);
            item.setInvulnerable(true);
            item.setMetadata(META_KEY, new FixedMetadataValue(plugin, true));
        });

        return new BattleBus(startPoint, endPoint, speed, entity);
    }
}
