package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.repository.GameRepository;
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
    private final BossBarManager bossBarManager;
    private final GameRepository gameRepository;


    @Inject
    InGameState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, BossBarManager bossBarManager, GameRepository gameRepository) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
        this.bossBarManager = bossBarManager;
        this.gameRepository = gameRepository;
    }


    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        player.getPlayer().peek(p -> scoreboardManager.show(p, ScoreboardConfig::inGame));
        gameRepository.getGameContaining(player.getPlayerId())
                .peek(game -> bossBarManager.show(player.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        gameRepository.getGameContaining(player.getPlayerId())
                .peek(game -> bossBarManager.hide(player.getPlayerId(), game.getGameBossBar()));
    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
