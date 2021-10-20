package me.bristermitten.warzone.game.repository;

import com.google.inject.AbstractModule;

public class GameRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameRepository.class).to(GameStorage.class);
        bind(MutableGameRepository.class).to(GameStorage.class);
    }
}
