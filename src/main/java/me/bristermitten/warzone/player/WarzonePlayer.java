package me.bristermitten.warzone.player;

import io.vavr.control.Option;
import me.bristermitten.warzone.data.Ratio;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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

    public Option<Player> getPlayer() {
        return Option.of(Bukkit.getPlayer(playerId));
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
    }

    public Ratio getKDR() {
        return new Ratio(kills, deaths);
    }
}
