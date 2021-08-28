package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;

public interface ArgumentProcessor<T> extends TabCompleter, ArgumentContext<T>, ContextResolver<T, BukkitCommandExecutionContext> {
}
