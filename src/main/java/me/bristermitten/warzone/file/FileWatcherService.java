package me.bristermitten.warzone.file;

import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
public class FileWatcherService implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcherService.class);
    private final ConcurrentMap<Path, Set<FileWatcher>> watchers = new ConcurrentHashMap<>();

    private Thread thread;


    public synchronized void watch() {
        if (thread == null) {
            thread = new Thread(this, "Warzone File Watcher");
            thread.setDaemon(true);
        }
        if (thread.isAlive()) {
            return;
        }
        thread.start();
    }

    public void add(@NotNull FileWatcher watcher) {
        LOGGER.info("Registering new FileWatcher watching {}", watcher.watching());
        var mutableList = new HashSet<>(watchers.getOrDefault(watcher.watching(), Set.of()));
        mutableList.add(watcher);
        watchers.put(watcher.watching(), mutableList);
    }

    @Override
    public void run() {
        try (var watchService = FileSystems.getDefault().newWatchService()) {
            for (var watcherList : watchers.values()) {
                for (FileWatcher watcher : watcherList) {
                    Path toWatch = watcher.watching();
                    if (!Files.isDirectory(toWatch)) {
                        toWatch = toWatch.getParent();
                    }
                    toWatch.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                }

                boolean poll = true;
                while (poll) {
                    poll = pollEvents(watchService);
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private boolean pollEvents(@NotNull WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path at = (Path) key.watchable();

        for (WatchEvent<?> pollEvent : key.pollEvents()) {
            if (pollEvent.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                continue;
            }
            Set<FileWatcher> fileWatchers = watchers.get(at.resolve((Path) pollEvent.context()));
            if (fileWatchers == null) {
                return key.reset(); //not relevant
            }
            for (FileWatcher watcher : fileWatchers) {
                watcher.onModify().accept(pollEvent);
            }
        }
        return key.reset();
    }
}
