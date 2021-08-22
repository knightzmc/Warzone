package me.bristermitten.warzone.game.config;

import com.google.inject.AbstractModule;

public class GameConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameConfig.class).toProvider(GameConfigLoader.class);
    }
}
