package me.bristermitten.warzone.database;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class DatabaseAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new DatabaseModule();
    }
}
