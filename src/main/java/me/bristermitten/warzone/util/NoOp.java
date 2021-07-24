package me.bristermitten.warzone.util;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedRunnable;

/**
 * Contains utility methods for generating no-op functions because lambdas are ugly
 */
public class NoOp {
    private NoOp() {

    }

    public static <T> CheckedConsumer<T> consumer() {
        return unused -> {
        };
    }
    public static CheckedRunnable runnable() {
        return () -> {
        };
    }
}
