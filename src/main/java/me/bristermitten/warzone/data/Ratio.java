package me.bristermitten.warzone.data;

public record Ratio(int numerator, int denominator) {

    public double asDouble() {
        if (denominator == 0) {
            return numerator;
        }
        return (double) numerator / denominator;
    }
}
