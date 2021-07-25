package me.bristermitten.warzone.config.loading;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ObjectMapper {
    <T> @NotNull Try<T> map(Map<Object, Object> map, Class<T> to);
}
