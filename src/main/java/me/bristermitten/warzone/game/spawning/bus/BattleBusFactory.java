package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.data.Point;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import javax.inject.Inject;

public class BattleBusFactory {
    private final BattleBusMoveTask battleBusMoveTask;

    @Inject
    public BattleBusFactory(BattleBusMoveTask battleBusMoveTask) {
        this.battleBusMoveTask = battleBusMoveTask;
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
        var direction = startPoint.toLocation(world).toVector().subtract(endPoint.toLocation(world).toVector());

        var entity = world.spawn(startPoint.toLocation(world).setDirection(direction), ArmorStand.class, item -> {
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
