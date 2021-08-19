package me.bristermitten.warzone.player;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.player.state.PlayerStateChangeListener;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.PlayerStatesImpl;
import me.bristermitten.warzone.player.storage.PlayerDatabaseHook;
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import me.bristermitten.warzone.player.storage.PlayerStorage;

public class PlayerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerStorage.class).asEagerSingleton();
        bind(PlayerPersistence.class).to(PlayerDatabaseHook.class);
        bind(PlayerStates.class).to(PlayerStatesImpl.class);
        bind(PlayerManager.class).to(PlayerManagerImpl.class);

        bind(PlayerJoinQuitListener.class).asEagerSingleton();
        bind(PlayerStateChangeListener.class).asEagerSingleton();
        bind(PlayerKillDeathListener.class).asEagerSingleton();
    }
}
