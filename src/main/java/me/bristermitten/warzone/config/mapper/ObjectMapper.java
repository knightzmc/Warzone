package me.bristermitten.warzone.config.mapper;

import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ObjectMapper {
    <T> @NotNull Try<T> map(Map<Object, Object> map, Class<T> to);

    <T> @NotNull Map<Object, Object> map(T t);
}
