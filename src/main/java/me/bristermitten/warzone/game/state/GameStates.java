package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.state.States;

import javax.inject.Inject;

public record GameStates(InProgressState inProgressState,
                         InLobbyState inLobbyState,
                         IdlingState idlingState) implements States<Game, GameState> {
    @Inject
    public GameStates {
    }
}
