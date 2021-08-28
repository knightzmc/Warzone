package me.bristermitten.warzone.game.death;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.warzone.game.gulag.GulagDeathHandler;

public class DeathModule extends AbstractModule {
    @Override
    protected void configure() {
        var multibinder = Multibinder.newSetBinder(binder(), GameDeathHandler.class);
        multibinder.addBinding().to(GulagDeathHandler.class);
        multibinder.addBinding().to(StatisticsDeathHandler.class);
        multibinder.addBinding().to(XPDeathHandler.class);
    }
}
