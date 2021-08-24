package me.bristermitten.warzone.game.statistic;

import com.google.inject.AbstractModule;

public class GamePersistenceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GamePersistence.class).to(GameStatsDatabaseHook.class);
    }
}
