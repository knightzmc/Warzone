package me.bristermitten.warzone.data;

public record Ratio(int numerator, int denominator) {

    public double asDouble() {
        if (denominator == 0) {
            return numerator;
        }
        return (double) numerator / denominator;
    }

    public String format() {
        return "%d:%d".formatted(numerator, denominator);
    }
}
