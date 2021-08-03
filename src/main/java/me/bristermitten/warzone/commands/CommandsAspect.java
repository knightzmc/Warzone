package me.bristermitten.warzone.commands;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class CommandsAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new CommandsModule();
    }
}
