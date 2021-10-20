package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Function;

class GameStateManagerImpl implements me.bristermitten.warzone.game.state.GameStateManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameStateManagerImpl.class);
    private final GameStates states;

    @Inject
    public GameStateManagerImpl(GameStates states) {
        this.states = states;
    }

    @Override
    public void setState(Game game, Function<GameStates, GameState> stateFunction) {
        final var newState = stateFunction.apply(states);
        game.setCurrentState(newState);
        LOGGER.debug("Set state of game {} to {}", game, newState);
    }
}
