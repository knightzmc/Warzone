package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;

public class NullState implements PlayerState {
    public static final NullState INSTANCE = new NullState();

    private NullState() {
    }

    @Override
    public void onEnter(WarzonePlayer player) {
        // no op
    }

    @Override
    public void onLeave(WarzonePlayer player) {
        // no op
    }

    @Override
    public @NotNull ChatChannel getChannel() {
        throw new IllegalStateException("Player is in the null state!");
    }
}
