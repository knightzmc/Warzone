package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardManager;

import javax.inject.Inject;


public class InPreGameLobbyState implements PlayerState {


    private final ScoreboardManager scoreboardManager;

    @Inject
    public InPreGameLobbyState(ScoreboardManager scoreboardManager) {
        this.scoreboardManager = scoreboardManager;
    }


    @Override
    public void onStateJoin(WarzonePlayer player) {

    }

    @Override
    public void onStateLeave(WarzonePlayer player) {

    }
}
