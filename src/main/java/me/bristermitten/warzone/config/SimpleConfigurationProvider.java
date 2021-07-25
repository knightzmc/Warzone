package me.bristermitten.warzone.config;

import io.vavr.control.Option;
import me.bristermitten.warzone.config.loading.ConfigLoadException;
import me.bristermitten.warzone.config.loading.ConfigurationLoader;
import me.bristermitten.warzone.file.FileWatcher;
import me.bristermitten.warzone.file.FileWatcherService;
import me.bristermitten.warzone.util.Cached;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleConfigurationProvider<T> implements ConfigurationProvider<T> {
    private final Configuration<T> source;
    private final Set<Consumer<T>> invalidationHooks = ConcurrentHashMap.newKeySet();
    private Cached<T> cached;


    public SimpleConfigurationProvider(Configuration<T> source) {
        this.source = source;
    }

    @Inject
    public void init(@NotNull Plugin plugin, @NotNull ConfigurationLoader loader, @NotNull FileWatcherService service) {
        if (this.cached != null) {
            throw new IllegalStateException("Already initialized!");
        }
        var realizedPath = plugin.getDataFolder().toPath().resolve(source.path());
        service.add(new FileWatcher(
                realizedPath,
                ignored -> {
                    invalidationHooks.forEach(it -> it.accept(get()));
                    cached.invalidate();
                }
        ));
        this.cached = new Cached<>(() -> {
            if (!Files.exists(realizedPath)) {
                var inJar = Option.of(plugin.getResource(source.path()))
                        .getOrElseThrow(() -> new IllegalStateException("No resource named " + source.path() + " in jar!"));

                try {
                    Files.createDirectories(realizedPath.getParent());
                    Files.copy(inJar, realizedPath);
                } catch (IOException e) {
                    throw new ConfigLoadException(e);
                }
            }

            var loaded = loader.load(source.type(), realizedPath);
            if (loaded.isFailure()) {
                throw new ConfigLoadException(loaded.getCause());
            }
            return loaded.get();
        });
    }

    @Override
    public @Nullable T get() {
        return cached.get();
    }

    @Override
    public void addInvalidationHook(Consumer<T> onInvalidation) {
        this.invalidationHooks.add(onInvalidation);
    }
}