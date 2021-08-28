package me.bristermitten.warzone.commands.args;

import co.aikar.commands.*;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.lang.LangService;

import javax.inject.Inject;
import java.util.Map;

public class FreeArenaCondition implements ArgumentCondition<Arena> {
    private final ArenaManager arenaManager;
    private final LangService langService;

    @Inject
    public FreeArenaCondition(ArenaManager arenaManager, LangService langService) {
        this.arenaManager = arenaManager;
        this.langService = langService;
    }

    @Override
    public Class<Arena> getType() {
        return Arena.class;
    }

    @Override
    public String getId() {
        return "free";
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Arena value) throws InvalidCommandArgument {
        if (value == null) {
            return;
        }
        if (arenaManager.arenaIsInUse().test(value)) {
            langService.sendMessage(context.getIssuer().getIssuer(), langConfig -> langConfig.errorLang().arenaInUse(),
                    Map.of("{arena}", value.name()));
            throw new ConditionFailedException();
        }
    }
}
