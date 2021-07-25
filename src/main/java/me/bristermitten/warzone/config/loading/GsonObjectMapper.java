package me.bristermitten.warzone.config.loading;

import com.google.gson.Gson;
import com.google.inject.Inject;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record GsonObjectMapper(Gson gson) implements ObjectMapper {
    @Inject
    public GsonObjectMapper {
    }

    @Override
    public <T> @NotNull Try<T> map(Map<Object, Object> map, Class<T> to) {
        final var tree = gson.toJsonTree(map);
        return Try.of(() -> gson.fromJson(tree, to));
    }
}
