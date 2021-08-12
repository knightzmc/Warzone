package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;

public class InGulagState extends InGameState{
    @Inject
    InGulagState(ScoreboardManager scoreboardManager, ChatChannel channel) {
        super(scoreboardManager, channel);
    }
}
