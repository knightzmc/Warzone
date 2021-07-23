package me.bristermitten.warzone.config;

import com.google.inject.Provider;

/**
 * Provider for a configuration. As the configuration will likely be loaded from a file, this
 * is expected to cache values.
 *
 * @param <T> The type being provided
 */
public interface ConfigurationProvider<T> extends Provider<T> {
}
