package me.bristermitten.warzone.game.bossbar;

import me.bristermitten.warzone.game.Game;
import org.jetbrains.annotations.NotNull;

public record GameBossBar(@NotNull Game game) {
    public double getProgress() {
        return (double) game.getTimer().getTimeRemaining() / game.getTimer().getDuration();
    }
}
