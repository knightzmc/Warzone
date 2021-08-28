package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions;

public interface TabCompleter extends CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    String getId();
}
