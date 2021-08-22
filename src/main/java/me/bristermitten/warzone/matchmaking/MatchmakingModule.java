package me.bristermitten.warzone.matchmaking;

import com.google.inject.AbstractModule;

public class MatchmakingModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MatchmakingService.class).to(SimpleMatchmakingService.class);
    }
}
