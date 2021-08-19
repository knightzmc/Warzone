package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.data.AngledPoint;
import me.bristermitten.warzone.data.Region;
import me.bristermitten.warzone.data.WorldAngledPoint;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record ArenasConfigDAO(
        Map<String, ArenaConfigDAO> arenas,
        @SerializedName("lobby-spawnpoint") WorldAngledPoint lobbySpawnpoint,
        @SerializedName("pre-game-lobby-spawnpoint") WorldAngledPoint preGameLobbySpawnpoint
) {
    public static final Configuration<ArenasConfigDAO> CONFIG = new Configuration<>(ArenasConfigDAO.class, "arenas.yml");

    record ArenaConfigDAO(String world,
                                 @Nullable String permission,
                                 @Nullable Integer priority,
                                 @SerializedName("gulag") GulagConfigDAO gulagConfigDAO,
                                 @SerializedName("playable-area") Region playableArea,
                                 @SerializedName("loot") String lootTable,
                                 GameConfigDAO game
    ) {
        record GameConfigDAO(
                @SerializedName("time-limit") int timeLimit,
                @SerializedName("player-limit") int playerLimit,
                @SerializedName("border-damage") int borderDamage,
                @SerializedName("border-damage-time") int borderDamageTime,
                @SerializedName("chest-rate") float chestRate,
                @SerializedName("max-gulag-entries") @Nullable Integer maxGulagEntries,
                @SerializedName("boss-bar") BossBarConfigDAO bossBarConfigDAO
        ) {
            record BossBarConfigDAO(
                    @NotNull String format,
                    @Nullable BarColor color,
                    @Nullable BarStyle style,
                    @Nullable List<BarFlag> flags
                    ){}
        }

        record GulagConfigDAO(
                @SerializedName("fighting-area-1") AngledPoint fightingArea1,
                @SerializedName("fighting-area-2") AngledPoint fightingArea2,
                @SerializedName("spawn-area") AngledPoint spawnArea
        ) {
        }
    }
}
