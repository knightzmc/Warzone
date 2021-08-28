package me.bristermitten.warzone.lobby;

import me.bristermitten.warzone.bossbar.AbstractCustomBossBar;
import me.bristermitten.warzone.bossbar.BossBarConfig;
import me.bristermitten.warzone.game.config.GameConfig;

import javax.inject.Provider;
import java.util.Objects;

public class PreGameLobbyBossBar extends AbstractCustomBossBar {
    private final PreGameLobbyTimer timer;
    private final Provider<GameConfig> gameConfigProvider;

    public PreGameLobbyBossBar(PreGameLobbyTimer timer, Provider<GameConfig> gameConfigProvider) {
        this.timer = timer;
        this.gameConfigProvider = gameConfigProvider;
    }

    public float getProgress() {
        long timeRemaining = timer.getTimeRemaining();
        if (timeRemaining <= 0) {
            return 0;
        }
        return (float) timeRemaining / timer.getDuration();
    }

    @Override
    protected BossBarConfig getNewConfig() {
        return gameConfigProvider.get().gameStartTimerConfig().bossBarConfig()
                .withProgress(getProgress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreGameLobbyBossBar that)) return false;
        return Objects.equals(timer, that.timer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timer);
    }
}
