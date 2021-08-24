package me.bristermitten.warzone.bossbar;

import me.bristermitten.warzone.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class GameBossBar implements CustomBossBar {
    private final @NotNull Game game;
    private BossBarConfig previous = null;
    private boolean paused = false;

    public GameBossBar(@NotNull Game game) {
        this.game = game;
    }

    public float getProgress() {
        long timeRemaining = game.getTimer().getTimeRemaining();
        if (timeRemaining <= 0) {
            return 0;
        }
        return (float) timeRemaining / game.getTimer().getDuration();
    }

    @Override
    public BossBarConfig getBossBar() {
        if (paused && previous != null) {
            return previous;
        }
        previous = game().getArena().gameConfig().bossBarConfig()
                .withProgress(getProgress());
        return previous;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public @NotNull Game game() {
        return game;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (GameBossBar) obj;
        return Objects.equals(this.game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }

    @Override
    public String toString() {
        return "GameBossBar[" +
               "game=" + game + ']';
    }

}
