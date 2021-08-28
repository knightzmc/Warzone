package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.warzone.commands.args.*;
import me.bristermitten.warzone.leaderboard.LeaderboardCommand;
import me.bristermitten.warzone.party.PartyCommand;

public class CommandsModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PaperCommandManager.class)
                .toProvider(CommandManagerProvider.class)
                .asEagerSingleton();

        var commandBinder = Multibinder.newSetBinder(binder(), BaseCommand.class);
        commandBinder.addBinding().to(WarzoneCommand.class);
        commandBinder.addBinding().to(WarzoneAdminCommand.class);
        commandBinder.addBinding().to(PartyCommand.class);
        commandBinder.addBinding().to(LeaderboardCommand.class);

        var argProcessorBinder = Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<ArgumentProcessor<?>>() {
        }.getRawType()));
        argProcessorBinder.addBinding().to(ArenaArgumentProcessor.class);

        var conditionBinder = Multibinder.newSetBinder(binder(), Key.get(new TypeLiteral<ArgumentCondition<?>>() {
        }.getRawType()));
        conditionBinder.addBinding().to(FreeArenaCondition.class);
        conditionBinder.addBinding().to(InUseArenaCondition.class);
    }
}
