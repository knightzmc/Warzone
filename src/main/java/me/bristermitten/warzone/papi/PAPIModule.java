package me.bristermitten.warzone.papi;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class PAPIModule extends AbstractModule {
    @Override
    protected void configure() {
        Multibinder.newSetBinder(binder(), WarzonePlaceholder.class)
                .addBinding().to(PartyMembersPlaceholder.class);
    }
}
