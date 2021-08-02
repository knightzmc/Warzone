package me.bristermitten.warzone.util;

import org.jetbrains.annotations.Nullable;

public class Cast {
    private Cast() {

    }

    public static <T> @Nullable T safe(@Nullable Object o, Class<T> clazz) {
        if (o == null) {
            return null;
        }
        if (!clazz.isAssignableFrom(o.getClass())) {
            return null;
        }
        return clazz.cast(o);
    }
}
