package me.bristermitten.warzone.commands.args;

import co.aikar.commands.*;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.lang.LangConfig;

import javax.inject.Inject;
import javax.inject.Provider;

public class InUseArenaCondition implements ArgumentCondition<Arena> {
    private final ArenaManager arenaManager;
    private final ChatFormatter chatFormatter;
    private final Provider<LangConfig> langConfigProvider;

    @Inject
    public InUseArenaCondition(ArenaManager arenaManager, ChatFormatter chatFormatter, Provider<LangConfig> langConfigProvider) {
        this.arenaManager = arenaManager;
        this.chatFormatter = chatFormatter;
        this.langConfigProvider = langConfigProvider;
    }

    @Override
    public Class<Arena> getType() {
        return Arena.class;
    }

    @Override
    public String getId() {
        return "inUse";
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Arena value) throws InvalidCommandArgument {
        if (value == null) {
            return;
        }
        if (!arenaManager.arenaIsInUse().test(value)) {
            var formatted = chatFormatter.withHooks((message, player) -> message.replace("{arena}", value.name()))
                    .preFormat(langConfigProvider.get().errorLang().arenaInUse(), context.getIssuer().getPlayer());
            throw new ConditionFailedException(formatted);
        }
    }
}
