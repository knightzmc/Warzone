package me.bristermitten.warzone.commands;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class CommandsAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new CommandsModule();
    }
}
