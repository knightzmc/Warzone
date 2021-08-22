package me.bristermitten.warzone.scoreboard;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.listener.ListenerBinding;

public class ScoreboardModule extends AbstractModule {
    @Override
    protected void configure() {
        ListenerBinding.bindListener(binder()).to(ScoreboardUpdateJoinQuitListener.class);
    }
}
