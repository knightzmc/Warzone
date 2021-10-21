package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.bossbar.BossBarConfig;
import me.bristermitten.warzone.data.AngledPoint;

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
            double minBorderSize,
            int borderDamageTime,
            float chestRate,
            int maxGulagEntries,
            BossBarConfig bossBarConfig,
            int maxChestY,
            long battleBusSpeed
    ) {

    }
}
