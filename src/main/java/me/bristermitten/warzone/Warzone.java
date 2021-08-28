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
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.protocol.ProtocolModule;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardModule;
import me.bristermitten.warzone.tags.TagsModule;
import me.bristermitten.warzone.vault.VaultModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Warzone extends JavaPlugin {

    private List<Persistence> persistences = List.of();

    @Override
    public void onEnable() {
        try {
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
                    , new VaultModule()
            );

            var injector = Guice.createInjector(modules);

            injector.getInstance(FileWatcherService.class).watch();

            persistences = List.of(
                    injector.getInstance(PlayerPersistence.class)
            );

            injector.getInstance(Key.get(new TypeLiteral<Set<EventListener>>() {
            })).forEach(eventListener -> Bukkit.getPluginManager().registerEvents(eventListener, this));

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

    @Override
    public void onDisable() {
        Future.sequence(persistences
                .map(Persistence::cleanup))
                .get(); // This needs to block!
    }
}
