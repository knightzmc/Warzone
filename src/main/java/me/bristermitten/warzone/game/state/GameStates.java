package me.bristermitten.warzone.game.state;

import javax.inject.Inject;
import javax.inject.Provider;

public record GameStates(Provider<InProgressState> inProgressStateProvider, Provider<InLobbyState> inLobbyStateProvider) {
    @Inject
    public GameStates {
    }
}
