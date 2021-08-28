package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions;

public interface ArgumentCondition<T> extends CommandConditions.ParameterCondition<T, BukkitCommandExecutionContext, BukkitCommandIssuer> {
    Class<T> getType();

    String getId();
}
