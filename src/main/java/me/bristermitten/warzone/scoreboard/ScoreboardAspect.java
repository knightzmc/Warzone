package me.bristermitten.warzone.scoreboard;

import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class ScoreboardAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new ScoreboardModule();
    }

    @Override
    public void finalizeInjections(@NotNull Injector injector) {
    }
}
