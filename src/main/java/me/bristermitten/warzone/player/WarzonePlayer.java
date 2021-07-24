package me.bristermitten.warzone.player;

import java.util.UUID;

public class WarzonePlayer {
    private final UUID playerId;
    private int kills;
    private int deaths;
    private int level;
    private long xp;

    public WarzonePlayer(UUID playerId) {
        this.playerId = playerId;
    }

    public WarzonePlayer(UUID playerId, int kills, int deaths, int level, long xp) {
        this.playerId = playerId;
        this.kills = kills;
        this.deaths = deaths;
        this.level = level;
        this.xp = xp;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getLevel() {
        return level;
    }

    public long getXp() {
        return xp;
    }
}
