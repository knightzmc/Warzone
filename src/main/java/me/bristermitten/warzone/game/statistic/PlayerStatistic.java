package me.bristermitten.warzone.game.statistic;

import java.util.List;

public record PlayerStatistic(
        int shotsFired,
        List<String> weaponsPickedUp,
        double accuracy,
        int medkitsUsed,
        int timesReloaded
) {
}
