package me.bristermitten.warzone.game.spawning.bus;

import me.bristermitten.warzone.data.WorldAngledPoint;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record BattleBus(@NotNull WorldAngledPoint startPoint, @NotNull WorldAngledPoint endPoint, long speed,
                        @NotNull Entity busEntity) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleBus battleBus)) return false;
        return speed == battleBus.speed && startPoint.equals(battleBus.startPoint) && endPoint.equals(battleBus.endPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, endPoint, speed);
    }
}
