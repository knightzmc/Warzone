package me.bristermitten.warzone.config;

import me.bristermitten.warzone.config.loading.ConfigurationReader;
import me.bristermitten.warzone.config.saving.ConfigurationWriter;

import javax.inject.Inject;
import java.nio.file.Path;

public class ConfigReaderWriter {
    private final ConfigurationReader loader;
    private final ConfigurationWriter saver;

    @Inject
    public ConfigReaderWriter(ConfigurationReader loader, ConfigurationWriter saver) {
        this.loader = loader;
        this.saver = saver;
    }

    public <T> T readFrom(Class<T> type, Path path, Path defaultPath) {
        var loaded = loader.load(type, path, defaultPath);
        return loaded.getOrElseThrow(ConfigIOException::new);
    }

    public <T> void writeTo(T t, Path path) {
        saver.save(t, path);
    }
}
