package me.bristermitten.warzone.papi;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class PAPIAspect implements Aspect {

    @Override
    public Module generateModule() throws IllegalStateException {
        return new PAPIModule();
    }

}
