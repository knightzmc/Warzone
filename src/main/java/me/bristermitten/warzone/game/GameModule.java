package me.bristermitten.warzone.game;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.bristermitten.warzone.game.bossbar.BossBarManager;
import me.bristermitten.warzone.game.bossbar.BossBarManagerImpl;
import me.bristermitten.warzone.game.init.ChunkLoadFiller;
import me.bristermitten.warzone.game.leavemenu.LeaveRequeueMenuFactory;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameManager.class).to(GameManagerImpl.class);
        bind(ChunkLoadFiller.class).asEagerSingleton();

        bind(BossBarManager.class).to(BossBarManagerImpl.class);

        install(new FactoryModuleBuilder()
                .build(LeaveRequeueMenuFactory.class));
    }
}
