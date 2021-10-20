package me.bristermitten.warzone.game.statistic;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record PlayerStatistic(
        int shotsFired,
        int shotsHit,
        @Unmodifiable List<String> weaponsPickedUp,
        int medkitsUsed,
        int timesReloaded
) {
}
