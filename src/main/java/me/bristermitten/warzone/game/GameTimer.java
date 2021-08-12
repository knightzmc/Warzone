package me.bristermitten.warzone.game;

public class GameTimer {
    private final long startTimeMillis;
    private final long endTimeMillis;

    public GameTimer(long durationMillis) {
        this.startTimeMillis = System.currentTimeMillis();
        this.endTimeMillis = startTimeMillis + durationMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getTimeRemaining() {
        return endTimeMillis - System.currentTimeMillis();
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }
}
