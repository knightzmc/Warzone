package me.bristermitten.warzone.player.state;

import javax.inject.Inject;

public record PlayerStates(InGameState inGameState, InLobbyState inLobbyState, InPreGameLobbyState inPreGameLobbyState,
                           OfflineState offlineState) {
    @Inject
    public PlayerStates {
    }
}
