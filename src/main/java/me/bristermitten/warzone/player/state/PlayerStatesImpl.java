package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.state.game.InGulagArenaState;
import me.bristermitten.warzone.player.state.game.InGulagQueuingState;
import me.bristermitten.warzone.player.state.game.SpectatingState;

import javax.inject.Inject;

public record PlayerStatesImpl(AliveState aliveState,
                               InGulagArenaState inGulagArenaState,
                               InGulagQueuingState inGulagQueuingState,
                               SpectatingState spectatingState,
                               InLobbyState inLobbyState,
                               InPreGameLobbyState inPreGameLobbyState,
                               OfflineState offlineState) implements PlayerStates {
    @Inject
    public PlayerStatesImpl {
    }
}
