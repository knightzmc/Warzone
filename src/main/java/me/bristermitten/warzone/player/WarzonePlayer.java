package me.bristermitten.warzone.player;

import io.vavr.control.Option;
import me.bristermitten.warzone.data.Ratio;
import me.bristermitten.warzone.player.state.NullState;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.state.Stateful;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class WarzonePlayer implements Stateful<WarzonePlayer, PlayerState> {
    private final UUID playerId;
    private int kills;
    private int deaths;
    private int level;
    private int wins;
    private int losses;
    private long xp;
    private @NotNull PlayerState currentState = NullState.INSTANCE;

    public WarzonePlayer(UUID playerId, int kills, int deaths, int level, int wins, int losses, long xp) {
        this.playerId = playerId;
        this.kills = kills;
        this.deaths = deaths;
        this.level = level;
        this.wins = wins;
        this.losses = losses;
        this.xp = xp;
    }

    public WarzonePlayer(UUID playerId) {
        this.playerId = playerId;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Option<Player> getPlayer() {
        return Option.of(Bukkit.getPlayer(playerId));
    }

    public @NotNull OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(playerId);
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

    public @NotNull Ratio getKDR() {
        return new Ratio(kills, deaths);
    }

    public @NotNull Ratio getWLR() {
        return new Ratio(wins, losses);
    }

    public @NotNull PlayerState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(@NotNull PlayerState currentState) {
        this.currentState.onLeave(this);
        this.currentState = currentState;
        this.currentState.onEnter(this);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarzonePlayer that)) return false;
        return getPlayerId().equals(that.getPlayerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayerId());
    }
}
