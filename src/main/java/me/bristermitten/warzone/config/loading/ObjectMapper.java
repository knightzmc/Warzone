package me.bristermitten.warzone.config.loading;

import io.vavr.control.Try;

import java.util.Map;

public interface ObjectMapper {
    <T> Try<T> map(Map<Object, Object> map, Class<T> to);
}
