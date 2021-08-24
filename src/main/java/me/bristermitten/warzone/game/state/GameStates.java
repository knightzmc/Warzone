package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.state.States;

import javax.inject.Inject;

public record GameStates(InProgressState inProgressStateProvider,
                         InLobbyState inLobbyStateProvider,
                         IdlingState idlingState) implements States<Game, GameState> {
    @Inject
    public GameStates {
    }
}
