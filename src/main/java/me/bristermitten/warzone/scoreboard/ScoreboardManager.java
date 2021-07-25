package me.bristermitten.warzone.scoreboard;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;

public class ScoreboardManager {
    private final Provider<ScoreboardConfig> configProvider;
    private final ScoreboardRenderer renderer;

    @Inject
    public ScoreboardManager(Provider<ScoreboardConfig> configProvider, ScoreboardRenderer renderer) {
        this.configProvider = configProvider;
        this.renderer = renderer;
    }

    public void show(@NotNull Player player, Function<ScoreboardConfig, ScoreboardTemplate> templateFunction) {
        renderer.show(templateFunction.apply(configProvider.get()), player);
    }

    public void show(@NotNull Player player, ScoreboardTemplate template) {
        show(player, ignored -> template);
    }

}
