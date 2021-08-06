package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InProgressState;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.party.Party;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class GameManager {
    private final Set<Game> gamesInProgress = new HashSet<>();
    private final LangService langService;

    private final GameStates states;

    @Inject
    public GameManager(LangService langService, GameStates states) {
        this.langService = langService;
        this.states = states;
    }

    public void addToGame(Game game, Party party) {
        if (game.getState() instanceof InProgressState) {
            return; // TODO this should never really happen so i'm not too concerned about a user friendly error message here
        }
        if (game.getState() instanceof IdlingState) {
            // fire it up!
            game.setCurrentState(states.inLobbyStateProvider().get());
            game.getPlayers().add(party);
            
        }
    }
}
