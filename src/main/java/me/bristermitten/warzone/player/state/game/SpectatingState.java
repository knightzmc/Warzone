package me.bristermitten.warzone.player.state.game;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;

public class SpectatingState extends InGameState {
    @Inject
    SpectatingState(ScoreboardManager scoreboardManager, @Named("inGame") ChatChannel channel) {
        super(scoreboardManager, channel);
    }

    @Override
    public void onEnter(@NotNull WarzonePlayer warzonePlayer) {
        super.onEnter(warzonePlayer);
        warzonePlayer.getPlayer().peek(player -> {
            player.setAllowFlight(true);
            player.setFlying(true);
        });
        // TODO remove from game and stuff
    }
}
