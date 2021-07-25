package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;
import javax.inject.Named;


public class InPreGameLobbyState implements PlayerState {


    private final ScoreboardManager scoreboardManager;
    private final ChatChannel channel;

    @Inject
    public InPreGameLobbyState(ScoreboardManager scoreboardManager, @Named("preGameLobby") ChatChannel channel) {
        this.scoreboardManager = scoreboardManager;
        this.channel = channel;
    }


    @Override
    public void onStateJoin(WarzonePlayer player) {

    }

    @Override
    public void onStateLeave(WarzonePlayer player) {

    }

    @Override
    public ChatChannel getChannel() {
        return channel;
    }
}
