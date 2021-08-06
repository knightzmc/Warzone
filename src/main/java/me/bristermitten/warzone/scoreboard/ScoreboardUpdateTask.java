package me.bristermitten.warzone.scoreboard;

import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;

public class ScoreboardUpdateTask extends Task {
    private final Provider<ScoreboardConfig> config;

    private final ScoreboardManager manager;
    private final Plugin plugin;

    @Inject
    public ScoreboardUpdateTask(Provider<ScoreboardConfig> config, ScoreboardManager manager, Plugin plugin) {
        this.config = config;
        this.manager = manager;
        this.plugin = plugin;
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            manager.updateScoreboards();
            if (running) {
                schedule();
            }
        }, config.get().updateTime());
    }
}
