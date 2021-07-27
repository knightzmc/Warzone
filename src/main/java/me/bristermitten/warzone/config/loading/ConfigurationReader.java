package me.bristermitten.warzone.config.loading;

import com.google.inject.Inject;
import io.vavr.Tuple;
import io.vavr.control.Try;
import me.bristermitten.warzone.config.ConfigurationUpdater;
import me.bristermitten.warzone.config.mapper.ObjectMapper;

import java.nio.file.Path;

public record ConfigurationReader(ObjectLoader loader,
                                  ObjectMapper mapper,
                                  ConfigurationUpdater updater) {

    @Inject
    public ConfigurationReader {
    }

    public <T> Try<T> load(Class<T> type, Path source, Path defaultResource) {
        return Try.of(() -> loader.load(source))
                .mapTry(map -> Tuple.of(map, loader.load(defaultResource)))
                .map(t -> updater.update(t._2, t._1))
                .flatMapTry(map -> mapper.map(map, type));
    }


}
