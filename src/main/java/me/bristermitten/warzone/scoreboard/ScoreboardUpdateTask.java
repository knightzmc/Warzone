package me.bristermitten.warzone.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import javax.inject.Inject;
import javax.inject.Provider;

public class ScoreboardUpdateTask {
    private final Provider<ScoreboardConfig> config;

    private final ScoreboardManager manager;
    private final Plugin plugin;

    private boolean running = false;

    private BukkitTask task;

    @Inject
    public ScoreboardUpdateTask(Provider<ScoreboardConfig> config, ScoreboardManager manager, Plugin plugin) {
        this.config = config;
        this.manager = manager;
        this.plugin = plugin;
    }

    private void schedule() {
        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            manager.updateScoreboards();
            if (running) {
                schedule();
            }
        }, config.get().updateTime());
    }

    public void start() {
        if (task != null) {
            return; // already running
        }
        running = true;
        schedule();
    }

    public void pause() {
        running = false;
        if (task != null) {
            task.cancel();
        }
        task = null;
    }
}
