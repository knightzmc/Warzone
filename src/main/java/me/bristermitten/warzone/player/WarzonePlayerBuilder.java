package me.bristermitten.warzone.player;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WarzonePlayerBuilder {
    private UUID playerId;
    private int kills;
    private int deaths;
    private int level;
    private int wins;
    private int losses;
    private long xp;

    public @NotNull WarzonePlayerBuilder setPlayerId(UUID playerId) {
        this.playerId = playerId;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setKills(int kills) {
        this.kills = kills;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setDeaths(int deaths) {
        this.deaths = deaths;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setLevel(int level) {
        this.level = level;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    public @NotNull WarzonePlayerBuilder setXp(long xp) {
        this.xp = xp;
        return this;
    }

    public @NotNull WarzonePlayer createWarzonePlayer() {
        return new WarzonePlayer(playerId, kills, deaths, level, wins, losses, xp);
    }
}
