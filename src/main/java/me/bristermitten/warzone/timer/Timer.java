package me.bristermitten.warzone.timer;

public abstract class Timer {
    protected long durationMillis;
    protected long startTimeMillis;
    protected long endTimeMillis;

    public boolean hasStarted() {
        return startTimeMillis != 0;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public long getDuration() {
        return durationMillis;
    }

    public long getEndTimeMillis() {
        return endTimeMillis;
    }

    public void start() {
        if (hasStarted()) {
            throw new IllegalStateException("Already started");
        }
        this.startTimeMillis = System.currentTimeMillis();
        this.endTimeMillis = startTimeMillis + durationMillis;
    }

    public long getTimeRemaining() {
        if (!hasStarted()) {
            throw new IllegalStateException("Timer not started yet");
        }
        return endTimeMillis - System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Timer{" +
               "durationMillis=" + durationMillis +
               ", startTimeMillis=" + startTimeMillis +
               ", endTimeMillis=" + endTimeMillis +
               '}';
    }
}
