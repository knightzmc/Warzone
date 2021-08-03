package me.bristermitten.warzone.chat;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.jetbrains.annotations.NotNull;

public class ChatAspect implements Aspect {
    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new ChatModule();
    }
}
