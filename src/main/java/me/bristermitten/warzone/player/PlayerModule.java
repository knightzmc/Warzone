package me.bristermitten.warzone.player;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.listener.ListenerBinding;
import me.bristermitten.warzone.player.state.PlayerStates;
import me.bristermitten.warzone.player.state.PlayerStatesImpl;
import me.bristermitten.warzone.player.state.game.SpectatingStateHotbarItemListener;
import me.bristermitten.warzone.player.storage.PlayerDatabaseHook;
import me.bristermitten.warzone.player.storage.PlayerPersistence;

public class PlayerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlayerPersistence.class).to(PlayerDatabaseHook.class);
        bind(PlayerStates.class).to(PlayerStatesImpl.class);
        bind(PlayerManager.class).to(PlayerManagerImpl.class);

        ListenerBinding.bindListener(binder()).to(PlayerJoinQuitListener.class);
        ListenerBinding.bindListener(binder()).to(PlayerKillDeathListener.class);
        ListenerBinding.bindListener(binder()).to(SpectatingStateHotbarItemListener.class);
    }
}
