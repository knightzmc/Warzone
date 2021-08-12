package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class SpectatingState extends InGameState {
    @Inject
    SpectatingState(ScoreboardManager scoreboardManager, ChatChannel channel) {
        super(scoreboardManager, channel);
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        super.onEnter(player);
        // TODO remove from game and stuff
    }
}
