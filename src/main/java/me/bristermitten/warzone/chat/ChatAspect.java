package me.bristermitten.warzone.chat;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

public class ChatAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new ChatModule();
    }
}
