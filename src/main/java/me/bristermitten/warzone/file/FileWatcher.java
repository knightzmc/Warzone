package me.bristermitten.warzone.file;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.Consumer;

public record FileWatcher(Path watching,
                          Consumer<WatchEvent<?>> onModify) {
}
