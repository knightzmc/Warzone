package me.bristermitten.warzone.util;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public class Numbers {
    public static Tuple2<Integer, Integer> minMax(int a, int b) {
        return Tuple.of(
                Math.min(a, b),
                Math.max(a, b)
        );
    }
}
