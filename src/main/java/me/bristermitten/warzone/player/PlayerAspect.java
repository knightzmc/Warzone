package me.bristermitten.warzone.player;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class PlayerAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new PlayerModule();
    }
}
