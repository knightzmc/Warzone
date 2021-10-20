package me.bristermitten.warzone.game;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import me.bristermitten.warzone.bossbar.BossBarManager;
import me.bristermitten.warzone.bossbar.BossBarManagerImpl;
import me.bristermitten.warzone.game.cleanup.GameCleanupModule;
import me.bristermitten.warzone.game.config.GameConfigModule;
import me.bristermitten.warzone.game.death.DeathModule;
import me.bristermitten.warzone.game.init.ChunkLoadFiller;
import me.bristermitten.warzone.game.repository.GameRepositoryModule;
import me.bristermitten.warzone.game.spawning.SpawningModule;
import me.bristermitten.warzone.game.state.GameStateModule;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.statistic.GamePersistenceModule;
import me.bristermitten.warzone.game.statistic.GameStatisticsListener;
import me.bristermitten.warzone.leavemenu.LeaveRequeueMenuFactory;
import me.bristermitten.warzone.listener.ListenerBinding;

public class GameModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(GameManager.class).to(GameManagerImpl.class);
        bind(GameJoinLeaveService.class).to(GameJoinLeaveServiceImpl.class);

        ListenerBinding.bindListener(binder()).to(ChunkLoadFiller.class);
        ListenerBinding.bindListener(binder()).to(GamePlayerRemoveListener.class);
        ListenerBinding.bindListener(binder()).to(GameStatisticsListener.class);

        bind(BossBarManager.class).to(BossBarManagerImpl.class);
        bind(IdlingState.class).toInstance(IdlingState.INSTANCE);

        install(new FactoryModuleBuilder()
                .build(LeaveRequeueMenuFactory.class));

        install(new FactoryModuleBuilder()
                .build(GameFactory.class));

        install(new GameConfigModule());
        install(new GamePersistenceModule());
        install(new SpawningModule());
        install(new DeathModule());
        install(new GameRepositoryModule());
        install(new GameStateModule());
        install(new GameCleanupModule());
    }
}
