package me.bristermitten.warzone.game.state;

import me.bristermitten.warzone.game.Game;

public class IdlingState implements GameState {
    public static final GameState INSTANCE = new IdlingState();

    private IdlingState() {
    }

    @Override
    public void onEnter(Game game) {

    }

    @Override
    public void onLeave(Game game) {

    }
}
