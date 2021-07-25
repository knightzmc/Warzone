package me.bristermitten.warzone.player;

import java.util.UUID;

public class WarzonePlayerBuilder {
    private UUID playerId;
    private int kills;
    private int deaths;
    private int level;
    private int wins;
    private int losses;
    private long xp;

    public WarzonePlayerBuilder setPlayerId(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public WarzonePlayerBuilder setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public WarzonePlayerBuilder setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }

    public WarzonePlayerBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public WarzonePlayerBuilder setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public WarzonePlayerBuilder setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    public WarzonePlayerBuilder setXp(long xp) {
        this.xp = xp;
        return this;
    }

    public WarzonePlayer createWarzonePlayer() {
        return new WarzonePlayer(playerId, kills, deaths, level, wins, losses, xp);
    }
}
