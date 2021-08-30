package me.bristermitten.warzone.config;

import me.bristermitten.warzone.file.FileWatcher;
import me.bristermitten.warzone.file.FileWatcherService;
import me.bristermitten.warzone.util.Cached;
import me.bristermitten.warzone.util.PathUtil;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
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
    public void init(@NotNull Plugin plugin, @NotNull ConfigReaderWriter readerWriter,
                     @NotNull FileWatcherService service) {
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
            Path pathInJar;
            try {
                final URL resource = plugin.getClass().getClassLoader().getResource(source.path());
                Objects.requireNonNull(resource, () -> "Could not find " + source.path() + " in jar!");
                pathInJar = PathUtil.resourceToPath(resource);
            } catch (IOException | URISyntaxException e) {
                throw new ConfigIOException("Could not load resource in jar for " + source.path(), e);
            }

            if (!Files.exists(realizedPath)) {
                try {
                    Files.createDirectories(realizedPath.getParent());
                    Files.copy(pathInJar, realizedPath);
                } catch (IOException e) {
                    throw new ConfigIOException(e);
                }
            }

            return readerWriter.readFrom(source.type(), realizedPath);
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
