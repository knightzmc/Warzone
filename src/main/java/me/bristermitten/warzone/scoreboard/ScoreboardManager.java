package me.bristermitten.warzone.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Singleton
public class ScoreboardManager {
    private final Provider<ScoreboardConfig> configProvider;
    private final ScoreboardRenderer renderer;

    private final Map<UUID, Function<ScoreboardConfig, ScoreboardTemplate>> activeScoreboards = new HashMap<>();

    @Inject
    public ScoreboardManager(Provider<ScoreboardConfig> configProvider, ScoreboardRenderer renderer) {
        this.configProvider = configProvider;
        this.renderer = renderer;
    }

    public void show(@NotNull Player player, Function<ScoreboardConfig, ScoreboardTemplate> templateFunction) {
        activeScoreboards.put(player.getUniqueId(), templateFunction); // save for later

        ScoreboardTemplate template = templateFunction.apply(configProvider.get());
        set(player, template);
    }

    private void set(@NotNull Player player, ScoreboardTemplate template) {
        renderer.show(template, player);
    }

    public void updateScoreboards() {
        activeScoreboards.forEach((uid, templateFunction) -> {
            Player player = Bukkit.getPlayer(uid);
            if (player != null) {
                set(player, templateFunction.apply(configProvider.get()));
            }
        });
    }

    public void show(@NotNull Player player, ScoreboardTemplate template) {
        show(player, ignored -> template);
    }

}
