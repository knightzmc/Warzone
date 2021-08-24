package me.bristermitten.warzone.game.statistic;

import java.util.List;

public record PlayerStatistic(
        int shotsFired,
        int shotsHit,
        List<String> weaponsPickedUp,
        int medkitsUsed,
        int timesReloaded
) {
}
