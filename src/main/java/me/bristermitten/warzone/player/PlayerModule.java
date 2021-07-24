package me.bristermitten.warzone.player;

import com.google.inject.AbstractModule;

public class PlayerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerStorage.class).asEagerSingleton();
        bind(PlayerPersistence.class).to(PlayerDatabaseHook.class);
    }
}
