package me.bristermitten.warzone.util;

import java.time.Duration;

public class DurationFormatter {
    private DurationFormatter() {

    }

    public static String format(long seconds) {
        var duration = Duration.ofSeconds(seconds);
        return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
    }
}
