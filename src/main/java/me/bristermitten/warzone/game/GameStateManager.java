package me.bristermitten.warzone.game;

import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.GameStates;
import me.bristermitten.warzone.state.StateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Function;

public class GameStateManager implements StateManager<Game, GameState, GameStates> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateManager.class);
    private final GameStates states;

    @Inject
    public GameStateManager(GameStates states) {
        this.states = states;
    }

    @Override
    public void setState(Game game, Function<GameStates, GameState> stateFunction) {
        final var newState = stateFunction.apply(states);
        game.setCurrentState(newState);
        LOGGER.debug("Set state of game {} to {}", game, newState);
    }
}
