package me.bristermitten.warzone.matchmaking;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class MatchmakingAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new AbstractModule() {
            @Override
            protected void configure() {
                binder().bind(MatchmakingService.class).to(SimpleMatchmakingService.class);
            }
        };
    }
}
