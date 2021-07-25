package me.bristermitten.warzone.player;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class PlayerAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new PlayerModule();
    }
}
