package me.bristermitten.warzone.util;

import org.jetbrains.annotations.NotNull;

public class OrdinalFormatter {
    private OrdinalFormatter() {

    }

    public static @NotNull String format(int ordinal) {
        if (ordinal <= 0) {
            throw new IllegalArgumentException("Must be greater than 0");
        }
        int digits = ordinal % 10;
        return ordinal + switch (digits) {
            case 0, 4, 5, 6, 7, 8, 9 -> "th";
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> throw new IllegalStateException("This can't happen, shut up compiler!");
        };
    }
}
