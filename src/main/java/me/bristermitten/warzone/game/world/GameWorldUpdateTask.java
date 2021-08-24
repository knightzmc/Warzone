package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.bossbar.GameBossBar;
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
    private final BossBarManager bossBarManager;
    private final GameManager gameManager;

    @Inject
    public GameWorldUpdateTask(Plugin plugin, BossBarManager bossBarManager, GameManager gameManager) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.gameManager = gameManager;
    }

    public void submit(Game game) {
        gamesToUpdate.add(game);
    }

    public void remove(Game game) {
        gamesToUpdate.remove(game);
        gameManager.getPlayers(game).forEach(warzonePlayer ->
                bossBarManager.hide(warzonePlayer.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bossBarManager.updateAll();
            for (Game game : gamesToUpdate) {
                game.getGameBorder().damagePlayersInBorder();
                gameManager.getPlayers(game).forEach(warzonePlayer ->
                        bossBarManager.show(warzonePlayer.getPlayerId(), game.getGameBossBar()));
            }

            if (running) {
                schedule();
            }
        }, 2);
    }
}
