package me.bristermitten.warzone.game.statistic;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
Stuff to store:
time spent in gulag
shots fired
weapons picked up
medkits used
times reloaded
total accuracy
 */

/**
 * Holds statistics for a certain {@link me.bristermitten.warzone.game.Game}
 */
public record GameStatistic(
        UUID gameId,
        String arenaName,
        Instant timeStarted,
        Instant timeFinished,
        Set<UUID> participants,
        Set<UUID> winners,
        Map<UUID, PlayerStatistic> playerStatistics
) {
}
