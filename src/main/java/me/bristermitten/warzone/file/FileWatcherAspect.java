package me.bristermitten.warzone.file;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class FileWatcherAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new AbstractModule() {
        };
    }

    @Override
    public void finalizeInjections(@NotNull Injector injector) {
        injector.getInstance(FileWatcherService.class).watch();
    }
}
