package me.bristermitten.warzone.database;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class DatabaseAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new DatabaseModule();
    }
}
