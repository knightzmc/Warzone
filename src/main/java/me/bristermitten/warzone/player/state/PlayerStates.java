package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.state.game.InGulagState;
import me.bristermitten.warzone.player.state.game.SpectatingState;

import javax.inject.Inject;

public record PlayerStates(AliveState aliveState,
                           InGulagState inGulagState,
                           SpectatingState spectatingState,
                           InLobbyState inLobbyState,
                           InPreGameLobbyState inPreGameLobbyState,
                           OfflineState offlineState) {
    @Inject
    public PlayerStates {
    }
}
