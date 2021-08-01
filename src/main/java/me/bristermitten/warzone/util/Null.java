package me.bristermitten.warzone.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class Null {
    private Null() {

    }

    public static <T> @NotNull T get(@Nullable T value, @NotNull T get) {
        if (value == null) {
            return get;
        }
        return value;
    }

    public static <T> @NotNull T get(@Nullable T value, @NotNull Supplier<T> get) {
        if (value == null) {
            return get.get();
        }
        return value;
    }
}
