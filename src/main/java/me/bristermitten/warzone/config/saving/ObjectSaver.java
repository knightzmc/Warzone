package me.bristermitten.warzone.config.saving;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface ObjectSaver {
    void save(Map<Object, Object> data, Path destination) throws IOException;
}
