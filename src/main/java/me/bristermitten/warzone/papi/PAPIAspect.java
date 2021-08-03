package me.bristermitten.warzone.papi;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class PAPIAspect implements Aspect {

    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new PAPIModule();
    }

}
