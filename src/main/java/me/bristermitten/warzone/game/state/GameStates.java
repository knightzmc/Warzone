package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.player.state.InGameState;

import javax.inject.Inject;
import javax.inject.Provider;

public record GameStates(Provider<InProgressState> inProgressStateProvider, Provider<InLobbyState> inLobbyStateProvider,
                         Provider<InGameState> inGameStateProvider) {
    @Inject
    public GameStates {
    }
}
