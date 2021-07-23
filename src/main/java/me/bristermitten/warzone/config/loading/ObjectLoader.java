package me.bristermitten.warzone.config.loading;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Responsible for reading some file and returning its values in a key-value format
 * Example implementations:
 * Json based using gson
 * Yaml based using Snakeyaml
 */
public interface ObjectLoader {
    Map<Object, Object> load(Path source) throws IOException;
}
