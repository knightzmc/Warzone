package me.bristermitten.warzone.game.spawning;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.game.spawning.bus.BusDismountListener;
import me.bristermitten.warzone.listener.ListenerBinding;

public class SpawningModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerSpawner.class).to(SwappablePlayerSpawner.class);
        ListenerBinding.bindListener(binder()).to(ElytraSpawningListener.class);
        ListenerBinding.bindListener(binder()).to(BusDismountListener.class);
    }
}
