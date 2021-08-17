package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

public class InGulagArenaState extends InGulagState {

    @Inject
    InGulagArenaState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel) {
        super(scoreboardManager, channel);
    }
}
