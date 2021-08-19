package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

/**
 * Updates the {@link GameBorder} and any {@link GameBossBar}'s
 */
@Singleton
public class GameWorldUpdateTask extends Task {
    private final Plugin plugin;
    private final Set<Game> gamesToUpdate = new HashSet<>();
    private final BossBarRenderer renderer;

    @Inject
    public GameWorldUpdateTask(Plugin plugin, BossBarRenderer renderer) {
        this.plugin = plugin;
        this.renderer = renderer;
    }

    public void submit(Game game) {
        gamesToUpdate.add(game);
    }

    public void remove(Game game) {
        gamesToUpdate.remove(game);
        renderer.removeBar(game.getGameBossBar());
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Game game : gamesToUpdate) {
                game.getGameBorder().damagePlayersInBorder();
                renderer.addBar(game.getGameBossBar());
            }

            if (running) {
                schedule();
            }
        }, 2);
    }
}
