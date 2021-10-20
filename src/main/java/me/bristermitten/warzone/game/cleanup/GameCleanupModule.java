package me.bristermitten.warzone.game.cleanup;

import com.google.inject.AbstractModule;

public class GameCleanupModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameCleanupService.class).to(GameCleanupServiceImpl.class);
    }
}
