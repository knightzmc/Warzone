package me.bristermitten.warzone;

import com.google.inject.Guice;
import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import me.bristermitten.warzone.arena.ArenasConfigDAO;
import me.bristermitten.warzone.aspect.Aspect;
import me.bristermitten.warzone.chat.ChatAspect;
import me.bristermitten.warzone.chat.ChatConfig;
import me.bristermitten.warzone.commands.CommandsAspect;
import me.bristermitten.warzone.config.ConfigurationAspect;
import me.bristermitten.warzone.database.DatabaseAspect;
import me.bristermitten.warzone.database.DatabaseConfig;
import me.bristermitten.warzone.database.Persistence;
import me.bristermitten.warzone.database.StorageException;
import me.bristermitten.warzone.file.FileWatcherAspect;
import me.bristermitten.warzone.game.GameAspect;
import me.bristermitten.warzone.game.leavemenu.LeaveRequeueMenu;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.leaderboard.LeaderboardAspect;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenu;
import me.bristermitten.warzone.loot.LootTablesConfig;
import me.bristermitten.warzone.matchmaking.MatchmakingAspect;
import me.bristermitten.warzone.papi.PAPIAspect;
import me.bristermitten.warzone.papi.WarzoneExpansion;
import me.bristermitten.warzone.player.PlayerAspect;
import me.bristermitten.warzone.player.storage.PlayerPersistence;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardAspect;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Warzone extends JavaPlugin {

    private List<Persistence> persistences = List.of();

    @Override
    public void onEnable() {
        try {
            var configAspect = new ConfigurationAspect(Set.of(
                    ScoreboardConfig.CONFIG
                    , DatabaseConfig.CONFIG
                    , XPConfig.CONFIG
                    , ChatConfig.CONFIG
                    , LangConfig.CONFIG
                    , LeaderboardMenu.CONFIG
                    , ArenasConfigDAO.CONFIG
                    , LootTablesConfig.CONFIG
                    , LeaveRequeueMenu.CONFIG
            ));

            var aspects = List.of(
                    configAspect,
                    new ScoreboardAspect()
                    , new PluginAspect(this)
                    , new FileWatcherAspect()
                    , new DatabaseAspect()
                    , new PlayerAspect()
                    , new ChatAspect()
                    , new CommandsAspect()
                    , new PAPIAspect()
                    , new LeaderboardAspect()
                    , new MatchmakingAspect()
                    , new GameAspect()
            );

            var modules = aspects.map(Aspect::generateModule);

            var injector = Guice.createInjector(modules);

            aspects.forEach(it -> it.finalizeInjections(injector));

            persistences = List.of(
                    injector.getInstance(PlayerPersistence.class)
            );

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
