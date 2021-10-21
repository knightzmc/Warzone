package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.data.WorldAngledPoint;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;
import java.util.Objects;

public class BattleBusFactory {
    private final BattleBusMoveTask battleBusMoveTask;

    @Inject
    public BattleBusFactory(BattleBusMoveTask battleBusMoveTask) {
        this.battleBusMoveTask = battleBusMoveTask;
    }

    /**
     * Creates and registers a new battle bus. The bus will immediately start moving after being spawned
     *
     * @param startPoint Where the bus starts
     * @param endPoint   Where the bus will end
     * @param speed      How many milliseconds it should take for the bus to move from its start point to its end point
     * @return a new BattleBus
     */
    public BattleBus createBus(WorldAngledPoint startPoint, WorldAngledPoint endPoint, long speed) {
        if (!startPoint.world().equals(endPoint.world())) {
            throw new IllegalArgumentException("Points must be in the same world");
        }

        var world = Bukkit.getWorld(startPoint.world());
        Objects.requireNonNull(world, "World not present");

        var entity = world.spawn(startPoint.toLocation(), ArmorStand.class, item -> {
            item.setInvisible(true);
            item.setInvulnerable(true);
            var busItem = new ItemStack(Material.RABBIT_HIDE, 1);
            busItem.editMeta(meta -> meta.setCustomModelData(15));
            item.getEquipment().setHelmet(busItem);
        });

        var bus = new BattleBus(startPoint, endPoint, speed, entity);
        battleBusMoveTask.add(bus);
        return bus;
    }
}
