package me.bristermitten.warzone.util;

import io.vavr.concurrent.Future;
import me.clip.placeholderapi.libs.kyori.adventure.util.Ticks;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class Schedule {
    private final Plugin plugin;

    @Inject
    public Schedule(Plugin plugin) {
        this.plugin = plugin;
    }

    public Future<Unit> runSync(Runnable r) {
        return Sync.run(r, plugin);
    }

    public Future<Unit> runAsync(Runnable r) {
        return Async.run(r, plugin);
    }

    public <T> Function<T, Future<Unit>> runSync(Consumer<T> c) {
        return t -> Sync.run(() -> {
            c.accept(t);
            return Unit.INSTANCE;
        }, plugin);
    }


    public Future<Unit> runLater(long durationMillis, Runnable runnable) {
        var future = new CompletableFuture<Unit>();
        var asTicks = durationMillis / Ticks.SINGLE_TICK_DURATION_MS;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            runnable.run();
            future.complete(Unit.INSTANCE);
        }, asTicks);
        return Future.fromCompletableFuture(future);
    }
}
