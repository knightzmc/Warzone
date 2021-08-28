package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.warzone.commands.args.ArenaArgumentProcessor;
import me.bristermitten.warzone.commands.args.ArgumentCondition;
import me.bristermitten.warzone.commands.args.ArgumentProcessor;
import me.bristermitten.warzone.commands.args.FreeArenaCondition;
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

        var argProcessorBinder = Multibinder.newSetBinder(binder(), ArgumentProcessor.class);
        argProcessorBinder.addBinding().to(ArenaArgumentProcessor.class);

        var conditionBinder = Multibinder.newSetBinder(binder(), ArgumentCondition.class);
        conditionBinder.addBinding().to(FreeArenaCondition.class);
    }
}
