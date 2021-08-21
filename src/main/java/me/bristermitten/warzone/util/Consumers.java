package me.bristermitten.warzone.util;

import java.util.function.Consumer;

public class Consumers {
    private Consumers() {

    }

    public static <T> Consumer<T> run(Runnable runnable) {
        return stinkyScopePollution -> runnable.run();
    }
}
