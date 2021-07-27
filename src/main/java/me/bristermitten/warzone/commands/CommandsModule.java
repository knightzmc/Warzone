package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class CommandsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PaperCommandManager.class).toProvider(CommandManagerProvider.class)
                .asEagerSingleton();

        var commandBinder = Multibinder.newSetBinder(binder(), BaseCommand.class);
        commandBinder.addBinding().to(WarzoneCommand.class);
        commandBinder.addBinding().to(PartyCommand.class);
    }
}
