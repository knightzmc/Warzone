package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import io.vavr.collection.List;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.commands.CommandErrors;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

public class ArenaArgumentProcessor implements ArgumentProcessor<Arena> {
    private final ArenaManager arenaManager;
    private final CommandErrors errors;

    @Inject
    public ArenaArgumentProcessor(ArenaManager arenaManager, CommandErrors errors) {
        this.arenaManager = arenaManager;
        this.errors = errors;
    }

    @Override
    public Class<Arena> getType() {
        return Arena.class;
    }

    @Override
    public String getId() {
        return "arenas";
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        List<Arena> arenas;
        if (context.hasConfig("inUse")) {
            arenas = arenaManager.getArenas().filter(arenaManager.arenaIsInUse());
        } else if (context.hasConfig("free")) {
            arenas = arenaManager.getArenas().filter(arenaManager.arenaIsInUse().negate());
        } else {
            arenas = arenaManager.getArenas();
        }
        return arenas.map(Arena::name).toJavaList();
    }

    @Override
    public Arena getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        var arg = context.popFirstArg();
        return arenaManager.getArenas().filter(arena -> arena.name().equals(arg))
                .headOption()
                .getOrElse(() -> errors.commandError(context, langConfig -> langConfig.errorLang().unknownArena(),
                        Map.of("{arena}", arg)));
    }
}
