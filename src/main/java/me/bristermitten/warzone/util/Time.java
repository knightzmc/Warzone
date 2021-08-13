package me.bristermitten.warzone.util;

public class Time {
    private Time() {

    }

    public static long millisToTicks(long millis) {
        return millis / 50;
    }
}
