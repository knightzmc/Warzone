package me.bristermitten.warzone.util;

import io.vavr.CheckedConsumer;
import io.vavr.CheckedRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * Contains utility methods for generating no-op functions because lambdas are ugly
 */
public class NoOp {
    private NoOp() {

    }

    public static <T> @NotNull CheckedConsumer<T> consumer() {
        return unused -> {
        };
    }
    public static @NotNull CheckedRunnable runnable() {
        return () -> {
        };
    }
}
