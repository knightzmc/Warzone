package me.bristermitten.warzone.game;

import java.util.Objects;
import java.util.UUID;

public class PlayerInformation {
    private final UUID player;
    private boolean isAlive = true;
    private int kills = 0;
    private int deathCount = 0;

    public PlayerInformation(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerInformation that)) return false;
        return getPlayer().equals(that.getPlayer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer());
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }
}
