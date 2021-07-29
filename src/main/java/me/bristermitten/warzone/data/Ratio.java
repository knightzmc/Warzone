package me.bristermitten.warzone.data;

import org.jetbrains.annotations.NotNull;

public record Ratio(int numerator, int denominator) implements Comparable<Ratio> {

    public double asDouble() {
        if (denominator == 0) {
            return numerator;
        }
        if (numerator == 0) {
            return -denominator;
        }
        if (numerator < 0) {
            return -(double) numerator / denominator;
        }
        return (double) numerator / denominator;
    }

    public String format() {
        return "%d:%d".formatted(numerator, denominator);
    }

    @Override
    public int compareTo(@NotNull Ratio o) {
        return Double.compare(asDouble(), o.asDouble());
    }
}
