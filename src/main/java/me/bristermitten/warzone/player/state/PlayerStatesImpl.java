package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.state.game.*;

import javax.inject.Inject;

public record PlayerStatesImpl(AliveState aliveState,
                               InGameSpawningState inGameSpawningState,
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
