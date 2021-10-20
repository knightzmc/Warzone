package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class InGulagState extends InGameState {
    @Inject
    InGulagState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, GameRepository gameRepository, BossBarManager bossBarManager) {
        super(scoreboardManager, channel, bossBarManager, gameRepository);
    }

}
