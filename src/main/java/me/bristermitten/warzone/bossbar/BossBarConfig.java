package me.bristermitten.warzone.bossbar;

import net.kyori.adventure.bossbar.BossBar;

import java.util.Set;

public record BossBarConfig(
        String name,
        float progress,
        BossBar.Color color,
        BossBar.Overlay overlay,
        Set<BossBar.Flag> flags
) {
    public BossBarConfig withProgress(float progress) {
        return new BossBarConfig(name, progress, color, overlay, flags);
    }
}
