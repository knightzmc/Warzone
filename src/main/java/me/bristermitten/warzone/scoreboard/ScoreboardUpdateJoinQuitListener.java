package me.bristermitten.warzone.scoreboard;

import me.bristermitten.warzone.listener.EventListener;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;

/**
 * Starts / stops the scoreboard update task based on if there are any players on the server or not
 */
public class ScoreboardUpdateJoinQuitListener implements EventListener {
    private final ScoreboardUpdateTask task;

    @Inject
    public ScoreboardUpdateJoinQuitListener(ScoreboardUpdateTask task) {
        this.task = task;
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
