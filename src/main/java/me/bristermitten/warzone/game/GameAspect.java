package me.bristermitten.warzone.game;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class GameAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new GameModule();
    }
}
