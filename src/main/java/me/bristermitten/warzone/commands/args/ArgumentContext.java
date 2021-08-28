package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;

public interface ArgumentContext <T>extends ContextResolver<T, BukkitCommandExecutionContext> {
    Class<T> getType();
}
