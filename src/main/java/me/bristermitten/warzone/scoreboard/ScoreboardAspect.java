package me.bristermitten.warzone.scoreboard;

import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class ScoreboardAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new ScoreboardModule();
    }

    @Override
    public void finalizeInjections(Injector injector) {
        System.out.println(injector.getInstance(ScoreboardConfig.class));
    }
}
