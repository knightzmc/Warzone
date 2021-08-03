package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Set;

public class CommandManagerProvider implements Provider<PaperCommandManager> {
    private final Plugin plugin;
    private final Set<BaseCommand> commands;

    @Inject
    public CommandManagerProvider(Plugin plugin, Set<BaseCommand> commands) {
        this.plugin = plugin;
        this.commands = commands;
    }

    @Override
    public @NotNull PaperCommandManager get() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(plugin);
        commands.forEach(paperCommandManager::registerCommand);
        return paperCommandManager;
    }
}
