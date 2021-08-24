package me.bristermitten.warzone.game.spawning;

import com.google.inject.AbstractModule;

public class SpawningModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerSpawner.class).to(SwappablePlayerSpawner.class);
    }
}
