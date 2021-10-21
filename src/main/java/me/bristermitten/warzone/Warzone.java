package me.bristermitten.warzone;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.chat.ChatConfig;
import me.bristermitten.warzone.chat.ChatModule;
import me.bristermitten.warzone.commands.CommandsModule;
import me.bristermitten.warzone.config.ConfigModule;
import me.bristermitten.warzone.database.DatabaseConfig;
import me.bristermitten.warzone.database.DatabaseModule;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.file.FileWatcherService;
import me.bristermitten.warzone.game.GameModule;
import me.bristermitten.warzone.game.config.GameConfigDAO;
import me.bristermitten.warzone.hooks.HookModule;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.leaderboard.LeaderboardModule;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenu;
import me.bristermitten.warzone.leavemenu.LeaveRequeueMenu;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.loot.LootTablesConfig;
import me.bristermitten.warzone.matchmaking.MatchmakingModule;
import me.bristermitten.warzone.papi.PAPIModule;
import me.bristermitten.warzone.papi.WarzoneExpansion;
import me.bristermitten.warzone.player.PlayerModule;
import me.bristermitten.warzone.player.storage.PlayerDatabaseHook;
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.protocol.ProtocolModule;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardModule;
import me.bristermitten.warzone.tags.TagsConfig;
import me.bristermitten.warzone.tags.TagsModule;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.LevelMatchFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Warzone extends JavaPlugin {

    public static final String PACKAGE_NAME = "me.bristermitten.warzone";
    private List<Persistence> persistences = List.of();

    @Override
    public void onEnable() {
        try {
            saveDefaultConfig();
            if (getConfig().getBoolean("debug")) {
                setupDebugLogging();
            }
            var configModule = new ConfigModule(Set.of(
                    ScoreboardConfig.CONFIG
                    , DatabaseConfig.CONFIG
                    , XPConfig.CONFIG
                    , ChatConfig.CONFIG
                    , LangConfig.CONFIG
                    , LeaderboardMenu.CONFIG
                    , ArenasConfigDAO.CONFIG
                    , LootTablesConfig.CONFIG
                    , LeaveRequeueMenu.CONFIG
                    , GameConfigDAO.CONFIG
                    , TagsConfig.CONFIG
            ));

            var modules = List.of(
                    configModule,
                    new ScoreboardModule()
                    , new PluginModule(this)
                    , new DatabaseModule()
                    , new PlayerModule()
                    , new ChatModule()
                    , new CommandsModule()
                    , new PAPIModule()
                    , new LeaderboardModule()
                    , new MatchmakingModule()
                    , new GameModule()
                    , new ProtocolModule()
                    , new TagsModule()
                    , new HookModule()
            );

            var injector = Guice.createInjector(modules);

            injector.getInstance(FileWatcherService.class).watch();

            persistences = List.of(
                    injector.getInstance(PlayerPersistence.class),
                    injector.getInstance(PlayerDatabaseHook.class),
                    injector.getInstance(PlayerStorage.class)
            );

            injector.getInstance(Key.get(new TypeLiteral<Set<EventListener>>() {
            })).forEach(eventListener -> eventListener.register(this));

            Future.sequence(persistences
                            .map(Persistence::initialise))
                    .onFailure(t -> {
                        throw new StorageException("Could not load data", t);
                    })
                    .onSuccess(v -> getSLF4JLogger().info("Successfully loaded all data!"));

            injector.getInstance(WarzoneExpansion.class)
                    .register();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setupDebugLogging() {
        Configuration configuration = ((LoggerContext) LogManager.getContext(false)).getConfiguration();

        configuration.removeLogger(PACKAGE_NAME); // Remove the existing logger, if it exists


        ConsoleAppender consoleAppender = ConsoleAppender.newBuilder()
                .setName("WarzoneConsole")
                .setLayout(PatternLayout.newBuilder().withPattern("%highlightError{[%level] %c{1}: %paperMinecraftFormatting{%msg}%n%xEx{full}}").build())
                .build();
        consoleAppender.start(); //Create a new appender that roughly matches the paper one

        configuration.addAppender(consoleAppender);
        LoggerConfig warzoneConsole = LoggerConfig.createLogger(
                true,
                Level.DEBUG,
                PACKAGE_NAME,
                null,
                new AppenderRef[]{
                        AppenderRef.createAppenderRef("WarzoneConsole", Level.DEBUG, null)
                },
                null,
                new DefaultConfiguration(),
                LevelMatchFilter.newBuilder()
                        .setLevel(Level.DEBUG)
                        .build()
        );
        warzoneConsole.addAppender(consoleAppender, Level.DEBUG, null);
        configuration.addLogger(
                PACKAGE_NAME,
                warzoneConsole
        );

        ((Logger) getLog4JLogger()).setLevel(Level.DEBUG);
    }

    @Override
    public void onDisable() {
        Future.sequence(persistences
                        .map(Persistence::cleanup))
                .get(); // This needs to block!
    }
}
