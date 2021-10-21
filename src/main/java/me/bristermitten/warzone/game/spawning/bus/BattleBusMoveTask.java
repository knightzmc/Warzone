package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.task.Task;
import net.kyori.adventure.util.Ticks;
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
        buses.put(bus, new BattleBusMovement(bus, System.currentTimeMillis(), bus.speed(), false));
    }

    public void setPaused(BattleBus battleBus, boolean paused) {
        var data = buses.get(battleBus);
        if (data == null) {
            return;
        }
        if (data.paused == paused) {
            return; // nothing to change so there's no need to make a new object
        }
        buses.put(battleBus, new BattleBusMovement(data.bus(), data.startTime(), data.timeRemaining(), paused));
    }

    public void remove(BattleBus bus) {
        buses.remove(bus);
    }

    public boolean move(BattleBus battleBus) {
        var currentPosition = buses.computeIfAbsent(battleBus, bus ->
                new BattleBusMovement(bus, System.currentTimeMillis(), bus.speed(), false));
        if (currentPosition.paused()) {
            return false;
        }

        // How long there is left until the bus has completely moved to its destination
        // This lets us calculate the distance from the start point that it needs to move to
        long millisRemaining = currentPosition.startTime() + battleBus.speed() - System.currentTimeMillis();
        if (millisRemaining <= 0) {
            battleBus.busEntity().remove();
            return true;
        }

        final var startPoint = battleBus.startPoint().toVector();
        final var endPoint = battleBus.endPoint().toVector();
        final long speed = currentPosition.bus().speed();

        double magnitudeInTicks = speed / (double) Ticks.SINGLE_TICK_DURATION_MS;
        var direction = endPoint.subtract(startPoint).multiply(1 / magnitudeInTicks);

        battleBus.busEntity().setVelocity(direction);

        buses.put(battleBus, new BattleBusMovement(battleBus, currentPosition.startTime(), millisRemaining, false));
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
            long timeRemaining,
            boolean paused
    ) {
    }
}
