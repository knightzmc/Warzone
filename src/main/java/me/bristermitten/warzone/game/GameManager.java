package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.PlayerManager;
import me.bristermitten.warzone.player.state.PlayerStates;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class GameManager {
    private final Set<Game> gamesInProgress = new HashSet<>();
    private final LangService langService;

    private final GameStates states;
    private final PlayerStates playerStates;
    private final PlayerManager playerManager;


    @Inject
    public GameManager(LangService langService, GameStates states, PlayerStates playerStates, PlayerManager playerManager) {
        this.langService = langService;
        this.states = states;
        this.playerStates = playerStates;
        this.playerManager = playerManager;
    }

    public void addToGame(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // this should never really happen so i'm not too concerned about a user friendly error message here
        }
        if (game.getState() instanceof IdlingState) {
            // fire it up!
            game.setCurrentState(states.inLobbyStateProvider().get());
        }

        if (game.getState() instanceof InLobbyState) {
            game.getPlayers().add(party);
            party.setLocked(true); // TODO unlock
            party.getAllMembers().forEach(uuid ->
                    playerManager.loadPlayer(uuid, player ->
                            playerManager.setState(player, playerStates.inPreGameLobbyState())));
        }
    }
}
