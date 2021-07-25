package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;


public class InLobbyState implements PlayerState {


    private final ScoreboardManager scoreboardManager;

    @Inject
    public InLobbyState(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }


    @Override
    public void onStateJoin(WarzonePlayer player) {
        player.getPlayer().peek(p -> scoreboardManager.show(p, ScoreboardConfig::lobby));
    }

    @Override
    public void onStateLeave(WarzonePlayer player) {

    }
}
