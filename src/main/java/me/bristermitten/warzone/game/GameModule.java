package me.bristermitten.warzone.game;

import com.google.inject.AbstractModule;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameManager.class).to(GameManagerImpl.class);
    }
}
