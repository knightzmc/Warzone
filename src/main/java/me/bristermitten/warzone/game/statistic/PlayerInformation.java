package me.bristermitten.warzone.game.statistic;

import java.util.List;
import java.util.UUID;

/**
 * Stores player information during a game
 * It can be assumed that any object of this type will be actively mutated by the plugin
 * Once a game finishes, this is transformed into a statistics report
 * <p>
 * Mutable counterpart of {@link PlayerStatistic}
 */
public class PlayerInformation {
    private final UUID player;
    private int deathCount;
    private int killCount;
    private int shotsFired;
    private int shotsHit;
    private int medkitsUsed;
    private int timesReloaded;
    private List<String> weaponsPickedUp;

    public PlayerInformation(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
        return player;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public void setShotsFired(int shotsFired) {
        this.shotsFired = shotsFired;
    }

    public int getShotsHit() {
        return shotsHit;
    }

    public void setShotsHit(int shotsHit) {
        this.shotsHit = shotsHit;
    }

    public int getMedkitsUsed() {
        return medkitsUsed;
    }

    public void setMedkitsUsed(int medkitsUsed) {
        this.medkitsUsed = medkitsUsed;
    }

    public int getTimesReloaded() {
        return timesReloaded;
    }

    public void setTimesReloaded(int timesReloaded) {
        this.timesReloaded = timesReloaded;
    }

    public List<String> getWeaponsPickedUp() {
        return weaponsPickedUp;
    }

    public void setWeaponsPickedUp(List<String> weaponsPickedUp) {
        this.weaponsPickedUp = weaponsPickedUp;
    }

    public PlayerStatistic createStatistics() {
        return new PlayerStatistic(shotsFired, shotsHit, List.copyOf(weaponsPickedUp), medkitsUsed, timesReloaded);
    }

    public int getDeathCount() {
        return deathCount;
    }

    public void setDeathCount(int deathCount) {
        this.deathCount = deathCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }
}
