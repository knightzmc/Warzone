package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class InGulagState extends InGameState {
    @Inject
    InGulagState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel) {
        super(scoreboardManager, channel);
    }

}
