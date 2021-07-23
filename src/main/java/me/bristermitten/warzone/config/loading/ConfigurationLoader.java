package me.bristermitten.warzone.config.loading;

import com.google.inject.Inject;
import io.vavr.control.Try;

import java.nio.file.Path;

public record ConfigurationLoader(ObjectLoader loader,
                                  ObjectMapper mapper) {

    @Inject
    public ConfigurationLoader {
    }

    public <T> Try<T> load(Class<T> type, Path source) {
        return Try.of(() -> loader.load(source))
                .flatMapTry(map -> mapper.map(map, type));
    }
}
