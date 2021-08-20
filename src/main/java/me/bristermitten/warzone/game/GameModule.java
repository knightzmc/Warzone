package me.bristermitten.warzone.game;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.game.init.ChunkLoadFiller;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameManager.class).to(GameManagerImpl.class);
        bind(ChunkLoadFiller.class).asEagerSingleton();
    }
}
