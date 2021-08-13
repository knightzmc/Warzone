package me.bristermitten.warzone.util;

import java.time.Duration;

public class DurationFormatter {
    private DurationFormatter() {

    }

    public static String format(long millis) {
        var duration = Duration.ofMillis(millis);
        return String.format("%02d:%02d", duration.toMinutesPart(), duration.toSecondsPart());
    }
}
