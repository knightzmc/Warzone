package me.bristermitten.warzone.scoreboard;

import javax.inject.Inject;
import javax.inject.Provider;

public class ScoreboardManager {
    private final Provider<ScoreboardConfig> configProvider;

    @Inject
    public ScoreboardManager(Provider<ScoreboardConfig> configProvider) {
        this.configProvider = configProvider;
    }


}
