package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;

public class InGameState implements PlayerState {

    private final ScoreboardManager scoreboardManager;

    private final ChatChannel channel;

    @Inject
    InGameState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
    }


    @Override
    public void onStateJoin(WarzonePlayer player) {
        player.getPlayer().peek(p -> scoreboardManager.show(p, ScoreboardConfig::inGame));
    }

    @Override
    public void onStateLeave(WarzonePlayer player) {

    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
