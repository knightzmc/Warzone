package me.bristermitten.warzone.game.state;

import com.google.inject.AbstractModule;

public class GameStateModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameStateManager.class).to(GameStateManagerImpl.class);
    }
}
