package me.bristermitten.warzone.game.world;

import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.bossbar.game.GameBossBar;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.player.state.InPreGameLobbyState;
import me.bristermitten.warzone.player.state.game.InGameState;
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
    private final GameRepository gameRepository;

    @Inject
    public GameWorldUpdateTask(Plugin plugin, BossBarManager bossBarManager, GameRepository gameRepository) {
        this.plugin = plugin;
        this.bossBarManager = bossBarManager;
        this.gameRepository = gameRepository;
    }

    public void submit(Game game) {
        gamesToUpdate.add(game);
    }

    public void remove(Game game) {
        gamesToUpdate.remove(game);
        gameRepository.getPlayers(game).forEach(warzonePlayer ->
                bossBarManager.hide(warzonePlayer.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bossBarManager.updateAll();
            gamesToUpdate.forEach(game -> {
                game.getGameBorder().damagePlayersInBorder();
                gameRepository.getPlayers(game).forEach(warzonePlayer -> {
                    if (warzonePlayer.getCurrentState() instanceof InPreGameLobbyState) {
                        bossBarManager.show(warzonePlayer.getPlayerId(), game.getPreGameLobbyTimer().getBossBar());
                    } else if (warzonePlayer.getCurrentState() instanceof InGameState) {
                        bossBarManager.show(warzonePlayer.getPlayerId(), game.getGameBossBar());
                    }
                });
            });

            if (running) {
                schedule();
            }
        }, 2);
    }
}
