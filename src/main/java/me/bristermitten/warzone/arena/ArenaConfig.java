package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.data.AngledPoint;
import net.kyori.adventure.bossbar.BossBar;

import java.util.Set;

public interface ArenaConfig {
    record GulagConfig(
            @SerializedName("fighting-area-1") AngledPoint fightingArea1,
            @SerializedName("fighting-area-2") AngledPoint fightingArea2,
            @SerializedName("spawn-area") AngledPoint spawnArea
    ) {
    }

    record GameConfig(
            int timeLimit,
            int playerLimit,
            int borderDamage,
            int borderDamageTime,
            float chestRate,
            int maxGulagEntries,
            BossBarConfig bossBarConfig
    ) {
        public record BossBarConfig(
                String format,
                BossBar.Color color,
                BossBar.Overlay style,
                Set<BossBar.Flag> flags
        ) {
        }
    }
}
