package me.bristermitten.warzone.papi;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class PAPIModule extends AbstractModule {
    @Override
    protected void configure() {
        var multibinder = Multibinder.newSetBinder(binder(), WarzonePlaceholder.class);
        multibinder.addBinding().to(PartyMembersPlaceholder.class);
        multibinder.addBinding().to(GameStatusPlaceholder.class);
        multibinder.addBinding().to(LeaderboardPlaceholder.class);
    }
}
