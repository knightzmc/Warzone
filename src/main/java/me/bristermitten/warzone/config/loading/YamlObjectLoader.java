package me.bristermitten.warzone.config.loading;

import com.google.inject.Inject;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public record YamlObjectLoader(Yaml yaml) implements ObjectLoader {
    @Inject
    public YamlObjectLoader {
    }

    @Override
    public Map<Object, Object> load(Path source) throws IOException {
        return yaml.load(Files.readString(source));
    }
}
