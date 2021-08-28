package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import me.bristermitten.warzone.commands.args.ArgumentCondition;
import me.bristermitten.warzone.commands.args.ArgumentProcessor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Set;

public class CommandManagerProvider implements Provider<PaperCommandManager> {
    private final Plugin plugin;
    private final Set<BaseCommand> commands;
    private final Set<ArgumentProcessor<?>> argumentProcessors;
    private final Set<ArgumentCondition<?>> argumentConditions;

    @Inject
    public CommandManagerProvider(Plugin plugin, Set<BaseCommand> commands, Set<ArgumentProcessor<?>> argumentProcessors, Set<ArgumentCondition<?>> argumentConditions) {
        this.plugin = plugin;
        this.commands = commands;
        this.argumentProcessors = argumentProcessors;
        this.argumentConditions = argumentConditions;
    }

    private <T> void registerProcessor(PaperCommandManager commandManager, ArgumentProcessor<T> processor) {
        commandManager.getCommandContexts().registerContext(processor.getType(), processor);
        commandManager.getCommandCompletions().registerAsyncCompletion(processor.getId(), processor);
    }

    private <T> void registerCondition(PaperCommandManager commandManager, ArgumentCondition<T> condition) {
        commandManager.getCommandConditions().addCondition(condition.getType(), condition.getId(), condition);
    }

    @Override
    public @NotNull PaperCommandManager get() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(plugin);
        for (ArgumentProcessor<?> argumentProcessor : argumentProcessors) {
            registerProcessor(paperCommandManager, argumentProcessor);
        }
        for (ArgumentCondition<?> argumentCondition : argumentConditions) {
            registerCondition(paperCommandManager, argumentCondition);
        }


        commands.forEach(paperCommandManager::registerCommand);
        return paperCommandManager;
    }
}
