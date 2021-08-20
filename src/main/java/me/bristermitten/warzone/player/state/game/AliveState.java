package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public class AliveState extends InGameState {
    private final GameManager gameManager;

    @Inject
    AliveState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, GameManager gameManager) {
        super(scoreboardManager, channel);
        this.gameManager = gameManager;
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        super.onEnter(player);
        gameManager.getGameContaining(player.getPlayerId())
                .peek(game -> game.getGameBossBar().addViewer(player.getPlayerId()));
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        super.onLeave(player);
        gameManager.getGameContaining(player.getPlayerId())
                .peek(game -> game.getGameBossBar().removeViewer(player.getPlayerId()));
    }
}
