package me.bristermitten.warzone.commands.args;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;

import javax.inject.Inject;
import java.util.Collection;

public class ArenaArgumentProcessor implements ArgumentProcessor<Arena> {
    private final ArenaManager arenaManager;

    @Inject
    public ArenaArgumentProcessor(ArenaManager arenaManager) {
        this.arenaManager = arenaManager;
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
        return arenaManager.getArenas().map(Arena::name).toJavaList();
    }

    @Override
    public Arena getContext(BukkitCommandExecutionContext bukkitCommandExecutionContext) throws InvalidCommandArgument {
        var arg = bukkitCommandExecutionContext.popFirstArg();
        return arenaManager.getArenas().filter(arena -> arena.name().equals(arg))
                .headOption()
                .getOrElseThrow(() -> new InvalidCommandArgument("Unknown arena " + arg));
    }
}
