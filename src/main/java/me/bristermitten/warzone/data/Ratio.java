package me.bristermitten.warzone.data;

import org.jetbrains.annotations.NotNull;

public record Ratio(int numerator, int denominator) implements Comparable<Ratio> {

    public double asDouble() {
        if (denominator == 0) {
            return numerator;
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
