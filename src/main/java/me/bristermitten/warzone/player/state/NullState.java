package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;

public class NullState implements PlayerState {
    public static final NullState INSTANCE = new NullState();

    private NullState() {
    }

    @Override
    public void onStateJoin(WarzonePlayer player) {
        // no op
    }

    @Override
    public void onStateLeave(WarzonePlayer player) {
        // no op
    }

    @Override
    public ChatChannel getChannel() {
        throw new IllegalStateException("Player is in the null state!");
    }
}
