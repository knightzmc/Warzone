package me.bristermitten.warzone.config;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Keeps a configuration "up to date" by copying over
 * any elements that aren't set from the default
 */
public class ConfigurationUpdater {
    public Map<Object, Object> update(@NotNull Map<Object, Object> source, Map<Object, Object> destination) {
        if (destination == null) {
            return source;
        }
        for (var entry : source.entrySet()) {
            var key = entry.getKey();
            var inSource = entry.getValue();
            if (inSource instanceof Map) {
                var inDestination = destination.get(key);
                if (!(inDestination instanceof Map)) {
                    inDestination = new HashMap<>();
                }
                //noinspection unchecked
                inSource = update((Map<Object, Object>) inSource, (Map<Object, Object>) inDestination);
            }
            Object finalInSource = inSource;
            destination.computeIfAbsent(key, lol -> finalInSource);
        }
        return destination;
    }


}
