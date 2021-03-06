package me.bristermitten.warzone.bossbar.game;

import me.bristermitten.warzone.bossbar.AbstractCustomBossBar;
import me.bristermitten.warzone.bossbar.BossBarConfig;
import me.bristermitten.warzone.game.Game;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class GameBossBar extends AbstractCustomBossBar {
    private final @NotNull Game game;

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
    protected BossBarConfig getNewConfig() {
        return game().getArena().gameConfig().bossBarConfig()
                .withProgress(getProgress());
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

}
