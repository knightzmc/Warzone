package me.bristermitten.warzone.game.statistic;

import java.time.Instant;
import java.util.*;

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
        Set<PlayerDeath> deaths,
        Map<UUID, PlayerStatistic> playerStatistics
) {
    public static class Builder {
        private UUID gameId;
        private String arenaName;
        private Instant timeStarted;
        private Instant timeFinished;
        private Set<UUID> participants = new HashSet<>();
        private Set<UUID> winners = new HashSet<>();
        private Set<PlayerDeath> deaths = new HashSet<>();
        private Map<UUID, PlayerStatistic> playerStatistics = new HashMap<>();

        public Builder setGameId(UUID gameId) {
            this.gameId = gameId;
            return this;
        }

        public Builder setArenaName(String arenaName) {
            this.arenaName = arenaName;
            return this;
        }

        public Builder setTimeStarted(Instant timeStarted) {
            this.timeStarted = timeStarted;
            return this;
        }

        public Builder setTimeFinished(Instant timeFinished) {
            this.timeFinished = timeFinished;
            return this;
        }

        public Builder setParticipants(Set<UUID> participants) {
            this.participants = participants;
            return this;
        }

        public Set<UUID> getParticipants() {
            return participants;
        }

        public Builder setWinners(Set<UUID> winners) {
            this.winners = winners;
            return this;
        }

        public Set<UUID> getWinners() {
            return winners;
        }

        public Builder setDeaths(Set<PlayerDeath> deaths) {
            this.deaths = deaths;
            return this;
        }

        public Set<PlayerDeath> getDeaths() {
            return deaths;
        }

        public Builder setPlayerStatistics(Map<UUID, PlayerStatistic> playerStatistics) {
            this.playerStatistics = playerStatistics;
            return this;
        }

        public Map<UUID, PlayerStatistic> getPlayerStatistics() {
            return playerStatistics;
        }

        public GameStatistic build() {
            return new GameStatistic(gameId, arenaName, timeStarted, timeFinished, participants, winners, deaths, playerStatistics);
        }
    }
}
