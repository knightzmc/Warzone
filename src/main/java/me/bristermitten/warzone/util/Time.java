package me.bristermitten.warzone.util;

public class Time {
    private Time() {

    }

    public static long millisToTicks(long millis) {
        return millis / 50;
    }
    public static long ticksToMillis(long ticks) {
        return ticks * 50;
    }
}
