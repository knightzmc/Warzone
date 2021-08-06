package me.bristermitten.warzone.task;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;

public abstract class Task {

    protected boolean running = false;

    @Nullable
    protected BukkitTask runningTask;

    protected abstract void schedule();

    public void start() {
        if (runningTask != null) {
            return; // already running
        }
        running = true;
        schedule();
    }

    public void pause() {
        running = false;
        if (runningTask != null) {
            runningTask.cancel();
        }
        runningTask = null;
    }
}
