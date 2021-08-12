package me.bristermitten.warzone.papi;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.GameTimer;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.util.DurationFormatter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Objects;

public class GameStatusPlaceholder implements WarzonePlaceholder {
    private final PlayerManager playerManager;
    private final GameManager gameManager;

    @Inject
    public GameStatusPlaceholder(PlayerManager playerManager, GameManager gameManager) {
        this.playerManager = playerManager;
        this.gameManager = gameManager;
    }

    @Override

    public @Nullable String onPlaceholderRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        if (!(offlinePlayer instanceof Player player) || !player.isOnline()) {
            return null;
        }
//        var warzonePlayer = playerManager.lookupPlayer(player.getUniqueId());

        return switch (params) {
            case "time_remaining" -> gameManager.getGameContaining(player.getUniqueId())
                    .map(Game::getTimer)
                    .map(GameTimer::getTimeRemaining)
                    .map(DurationFormatter::format)
                    .getOrElse("Not in game");
            case "players_remaining" -> gameManager.getGameContaining(player.getUniqueId())
                    .map(Game::getAlivePlayers)
                    .map(Objects::toString)
                    .getOrElse("Not in game");
            default -> null;
        };
    }
}
