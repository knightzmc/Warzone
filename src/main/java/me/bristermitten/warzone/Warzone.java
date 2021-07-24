package me.bristermitten.warzone;

import com.google.inject.Guice;
import me.bristermitten.warzone.aspect.Aspect;
import me.bristermitten.warzone.config.ConfigurationAspect;
import me.bristermitten.warzone.database.DatabaseAspect;
import me.bristermitten.warzone.database.DatabaseConfig;
import me.bristermitten.warzone.file.FileWatcherAspect;
import me.bristermitten.warzone.player.PlayerAspect;
import me.bristermitten.warzone.player.xp.XPConfig;
import me.bristermitten.warzone.scoreboard.ScoreboardAspect;
import me.bristermitten.warzone.scoreboard.ScoreboardConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Set;

public class Warzone extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            var configAspect = new ConfigurationAspect(Set.of(
                    ScoreboardConfig.CONFIG,
                    DatabaseConfig.CONFIG,
                    XPConfig.CONFIG
            ));

            var aspects = List.of(
                    configAspect,
                    new ScoreboardAspect()
                    , new PluginAspect(this)
                    , new FileWatcherAspect()
                    , new DatabaseAspect()
                    , new PlayerAspect()
            );

            var modules = aspects.stream().map(Aspect::generateModule).toList();

            var injector = Guice.createInjector(modules);

            aspects.forEach(it -> it.finalizeInjections(injector));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
