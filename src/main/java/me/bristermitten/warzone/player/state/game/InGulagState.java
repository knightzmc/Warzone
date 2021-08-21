package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class InGulagState extends InGameState {
    @Inject
    InGulagState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel, GameManager gameManager, BossBarManager bossBarManager) {
        super(scoreboardManager, channel, gameManager, bossBarManager);
    }

}
