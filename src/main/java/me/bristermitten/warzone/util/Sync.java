package me.bristermitten.warzone.util;

import io.vavr.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Sync {
    private Sync() {
    }

    public static Future<Unit> run(@NotNull Runnable r, @NotNull Plugin p) {
        return run(() -> {
            r.run();
            return Unit.INSTANCE;
        }, p);
    }

    public static <T> Future<T> run(@NotNull Supplier<T> t, @NotNull Plugin p) {
        if(Bukkit.isPrimaryThread()) {
            return Future.successful(t.get());
        }
        var javaFuture = new CompletableFuture<T>();
        Bukkit.getScheduler().runTask(p, () -> javaFuture.complete(t.get()));
        return Future.fromCompletableFuture(javaFuture);
    }
}
