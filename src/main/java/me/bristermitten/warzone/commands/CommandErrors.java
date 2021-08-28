package me.bristermitten.warzone.commands;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.lang.LangService;

import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;

public class CommandErrors {
    private final LangService langService;

    @Inject
    public CommandErrors(LangService langService) {
        this.langService = langService;
    }

    public <T> T commandError(BukkitCommandExecutionContext bukkitCommandExecutionContext, Function<LangConfig, String> message) {
        return commandError(bukkitCommandExecutionContext, message, Map.of());
    }

    public <T> T commandError(BukkitCommandExecutionContext bukkitCommandExecutionContext, Function<LangConfig, String> message,
                              Map<String, Object> replacements) {
        langService.sendMessage(bukkitCommandExecutionContext.getSender(), message, replacements);
        throw new InvalidCommandArgument(false);
    }
}
