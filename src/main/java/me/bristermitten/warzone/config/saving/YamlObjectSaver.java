package me.bristermitten.warzone.config.saving;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public record YamlObjectSaver(Yaml yaml) implements ObjectSaver {
    @Inject
    public YamlObjectSaver {
    }

    @Override
    public void save(Map<Object, Object> data, @NotNull Path destination) throws IOException {
        BufferedWriter output = Files.newBufferedWriter(destination);
        yaml.dump(data, output);
        output.close();
    }
}
