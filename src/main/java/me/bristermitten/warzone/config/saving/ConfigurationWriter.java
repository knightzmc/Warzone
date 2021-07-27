package me.bristermitten.warzone.config.saving;

import com.google.inject.Inject;
import io.vavr.control.Try;
import me.bristermitten.warzone.config.mapper.ObjectMapper;

import java.nio.file.Path;

public record ConfigurationWriter(ObjectSaver saver,
                                  ObjectMapper mapper) {

    @Inject
    public ConfigurationWriter {
    }

    public <T> Try<Void> save(T t, Path destination) {
        return Try.of(() -> mapper.map(t))
                .andThenTry(mapped -> saver.save(mapped, destination))
                .map(i -> null);
    }
}
