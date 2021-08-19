package me.bristermitten.warzone.player.state;

import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.state.game.AliveState;
import me.bristermitten.warzone.player.state.game.InGulagArenaState;
import me.bristermitten.warzone.player.state.game.InGulagQueuingState;
import me.bristermitten.warzone.player.state.game.SpectatingState;
import me.bristermitten.warzone.state.States;

public interface PlayerStates extends States<WarzonePlayer, PlayerState> {
    AliveState aliveState();

    InGulagArenaState inGulagArenaState();

    InGulagQueuingState inGulagQueuingState();

    SpectatingState spectatingState();

    InLobbyState inLobbyState();

    InPreGameLobbyState inPreGameLobbyState();

    OfflineState offlineState();
}
