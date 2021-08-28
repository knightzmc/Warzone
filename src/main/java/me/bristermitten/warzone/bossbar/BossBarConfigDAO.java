package me.bristermitten.warzone.bossbar;

import me.bristermitten.warzone.util.Null;
import net.kyori.adventure.bossbar.BossBar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record BossBarConfigDAO(
        @NotNull String format,
        @Nullable BossBar.Color color,
        @Nullable BossBar.Overlay style,
        @Nullable Set<BossBar.Flag> flags
) {
    public BossBarConfig loadBossBarConfig() {
        return new BossBarConfig(
                format(),
                1f,
                Null.get(color(), BossBar.Color.WHITE),
                Null.get(style(), BossBar.Overlay.PROGRESS),
                Null.get(flags(), Set.<BossBar.Flag>of()).stream().filter(Objects::nonNull).collect(Collectors.toSet())
        );
    }

}
