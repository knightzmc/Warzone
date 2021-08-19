package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.data.AngledPoint;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;

import java.util.List;

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
        record BossBarConfig(
                String format,
                BarColor color,
                BarStyle style,
                List<BarFlag> flags
        ) {
        }
    }
}
