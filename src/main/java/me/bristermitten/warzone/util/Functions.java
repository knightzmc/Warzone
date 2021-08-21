package me.bristermitten.warzone.util;

import java.util.function.Function;
import java.util.function.Supplier;

public class Functions {
    private Functions() {

    }

    public static <T> Function<T, Void> constant(Runnable runnable) {
        return constant(() -> {
            runnable.run();
            return null;
        });
    }

    public static <T, R> Function<T, R> constant(Supplier<R> r) {
        return stinkyScopePollution -> r.get();
    }

    public static <T, R> Function<T, R> constant(R r) {
        return stinkyScopePollution -> r;
    }
}
