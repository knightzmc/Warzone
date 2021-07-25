package me.bristermitten.warzone.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

/**
 * Starts / stops the scoreboard update task based on if there are any players on the server or not
 */
public class ScoreboardUpdateJoinQuitListener implements Listener {
    private final ScoreboardUpdateTask task;

    @Inject
    public ScoreboardUpdateJoinQuitListener(ScoreboardUpdateTask task, Plugin plugin) {
        this.task = task;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        task.start();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() == 1) { // will be empty after the event, bukkit dumb
            task.pause();
        }
    }

}
