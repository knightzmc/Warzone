package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class BattleBusMoveTask extends Task {
    private final Map<BattleBus, BattleBusMovement> buses = new HashMap<>();

    private final Plugin plugin;

    @Inject
    public BattleBusMoveTask(Plugin plugin) {
        this.plugin = plugin;
    }

    public void moveAll() {
        buses.keySet().removeIf(this::move);
    }

    public void add(BattleBus bus) {
        buses.put(bus, new BattleBusMovement(bus, System.currentTimeMillis(), bus.speed()));
    }

    public void remove(BattleBus bus) {
        buses.remove(bus);
    }

    public boolean move(BattleBus battleBus) {
        var currentPosition = buses.computeIfAbsent(battleBus, bus ->
                new BattleBusMovement(bus, System.currentTimeMillis(), bus.speed()));

        // How long there is left until the bus has completely moved to its destination
        // This lets us calculate the distance from the start point that it needs to move to
        long millisRemaining = currentPosition.startTime() + battleBus.speed() - System.currentTimeMillis();
        if (millisRemaining <= 0) {
            battleBus.busEntity().remove();
            return true;
        }
        // How far through its journey the bus is
        double proportion = (double) millisRemaining / currentPosition.timeRemaining();

        // Then use the proportion to generate a new vector
        var vectorDistance = battleBus.endPoint().toLocation().toVector().subtract(battleBus.startPoint().toLocation().toVector());
        var newVector = vectorDistance.clone().multiply(proportion);
        battleBus.busEntity().setVelocity(newVector);

        buses.put(battleBus, new BattleBusMovement(battleBus, currentPosition.startTime(), millisRemaining));
        return false;
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            moveAll();
            if (running) {
                schedule();
            }
        }, 1);
    }

    private record BattleBusMovement(
            BattleBus bus,
            long startTime,
            long timeRemaining
    ) {
    }
}
