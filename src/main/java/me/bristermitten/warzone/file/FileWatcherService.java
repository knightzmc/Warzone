package me.bristermitten.warzone.file;

import com.google.inject.Singleton;

import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static java.util.stream.Collectors.groupingByConcurrent;

@Singleton
public class FileWatcherService implements Runnable {
    private final ConcurrentMap<Path, List<FileWatcher>> watchers;

    private Thread thread;

    public FileWatcherService(List<FileWatcher> watchers) {
        this.watchers = watchers.stream().collect(groupingByConcurrent(FileWatcher::watching));
    }


    public synchronized void watch() {
        if (thread == null) {
            thread = new Thread(this, "Warzone File Watcher");
            thread.setDaemon(true);
        }
        thread.start();
    }

    @Override
    public void run() {
        try (var watchService = FileSystems.getDefault().newWatchService()) {
            for (var watcherList : watchers.values()) {
                for (FileWatcher watcher : watcherList) {
                    watcher.watching().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
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

    private boolean pollEvents(WatchService watchService) throws InterruptedException {
        WatchKey key = watchService.take();
        Path path = (Path) key.watchable();
        List<FileWatcher> fileWatchers = watchers.get(path);
        if (fileWatchers == null) {
            return key.reset(); //not relevant
        }
        for (WatchEvent<?> pollEvent : key.pollEvents()) {
            if (pollEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                for (FileWatcher watcher : fileWatchers) {
                    watcher.onModify().accept(pollEvent);
                }
            }
        }
        return key.reset();
    }
}
