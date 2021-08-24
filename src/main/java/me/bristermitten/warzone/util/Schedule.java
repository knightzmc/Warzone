package me.bristermitten.warzone.util;

import io.vavr.concurrent.Future;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.function.Consumer;
import java.util.function.Function;

public class Schedule {
    private final Plugin plugin;

    @Inject
    public Schedule(Plugin plugin) {
        this.plugin = plugin;
    }

    public Future<Void> runSync(Runnable r) {
        return Sync.run(r, plugin);
    }

    public <T> Function<T, Future<Void>> runSync(Consumer<T> c) {
        return t -> Sync.run(() -> c.accept(t), plugin);
    }
}
