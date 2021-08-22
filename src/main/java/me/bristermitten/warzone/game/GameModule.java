package me.bristermitten.warzone.game;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.game.bossbar.BossBarManagerImpl;
import me.bristermitten.warzone.game.config.GameConfigModule;
import me.bristermitten.warzone.game.init.ChunkLoadFiller;
import me.bristermitten.warzone.leavemenu.LeaveRequeueMenuFactory;
import me.bristermitten.warzone.listener.ListenerBinding;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameManager.class).to(GameManagerImpl.class);
        ListenerBinding.bindListener(binder()).to(ChunkLoadFiller.class);

        bind(BossBarManager.class).to(BossBarManagerImpl.class);

        install(new FactoryModuleBuilder()
                .build(LeaveRequeueMenuFactory.class));

        install(new GameConfigModule());
    }
}
