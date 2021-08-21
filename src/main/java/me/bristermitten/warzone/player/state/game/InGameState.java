package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.PlayerState;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class InGameState implements PlayerState {

    private final ScoreboardManager scoreboardManager;

    private final ChatChannel channel;
    private final GameManager gameManager;
    private final BossBarManager bossBarManager;


    @Inject
    InGameState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, GameManager gameManager, BossBarManager bossBarManager) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
        this.gameManager = gameManager;
        this.bossBarManager = bossBarManager;
    }


    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        player.getPlayer().peek(p -> scoreboardManager.show(p, ScoreboardConfig::inGame));
        gameManager.getGameContaining(player.getPlayerId())
                .peek(game -> bossBarManager.show(player.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        gameManager.getGameContaining(player.getPlayerId())
                .peek(game -> bossBarManager.hide(player.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
