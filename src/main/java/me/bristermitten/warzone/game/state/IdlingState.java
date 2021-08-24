package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;

/**
 * State of a game idling, i.e nothing is happening
 * A game will rarely be in this state as generally it's either in the lobby or in an actual game
 */
public class IdlingState implements GameState {
    public static final IdlingState INSTANCE = new IdlingState();

    private IdlingState() {
    }

    @Override
    public void onEnter(Game game) {

    }

    @Override
    public void onLeave(Game game) {

    }
}
