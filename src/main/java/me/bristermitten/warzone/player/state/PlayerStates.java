package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.game.*;
import me.bristermitten.warzone.state.States;

public interface PlayerStates extends States<WarzonePlayer, PlayerState> {
    AliveState aliveState();

    InGameSpawningState inGameSpawningState();

    InGulagArenaState inGulagArenaState();

    InGulagQueuingState inGulagQueuingState();

    SpectatingState spectatingState();

    InLobbyState inLobbyState();

    InPreGameLobbyState inPreGameLobbyState();

    OfflineState offlineState();
}
