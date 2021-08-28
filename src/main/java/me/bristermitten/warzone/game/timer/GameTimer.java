package me.bristermitten.warzone.game.timer;

public class GameTimer {
    private final long durationMillis;
    private long startTimeMillis;
    private long endTimeMillis;

    public GameTimer(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getDuration() {
        return durationMillis;
    }

    public boolean isInitialised() {
        return startTimeMillis != 0;
    }

     public long getTimeRemaining() {
        if (!isInitialised()) {
            throw new IllegalStateException("Timer not started yet");
        }
        return endTimeMillis - System.currentTimeMillis();
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void start() {
        if (isInitialised()) {
            throw new IllegalStateException("Already started");
        }
        this.startTimeMillis = System.currentTimeMillis();
        this.endTimeMillis = startTimeMillis + durationMillis;
    }
}
