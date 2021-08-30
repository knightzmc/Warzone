package me.bristermitten.warzone.config.loading;

import com.google.inject.Inject;
import io.vavr.control.Try;
import me.bristermitten.warzone.config.mapper.ObjectMapper;

import java.nio.file.Path;

public record ConfigurationReader(ObjectLoader loader,
                                  ObjectMapper mapper) {

    @Inject
    public ConfigurationReader {
    }

    public <T> Try<T> load(Class<T> type, Path source) {
        return Try.of(() -> loader.load(source))
                .flatMapTry(map -> mapper.map(map, type));
    }


}
