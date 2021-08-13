package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public class InGulagState extends InGameState{
    @Inject
    InGulagState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel) {
        super(scoreboardManager, channel);
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer player) {
        super.onEnter(player);
        // TODO send to gulag
    }
}
