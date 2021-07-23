package me.bristermitten.warzone.file;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class FileWatcherAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new AbstractModule() {
        };
    }

    @Override
    public void finalizeInjections(Injector injector) {
        injector.getInstance(FileWatcherService.class).watch();
    }
}
