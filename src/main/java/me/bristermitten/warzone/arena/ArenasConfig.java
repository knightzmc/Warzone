package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.data.AngledPoint;
import me.bristermitten.warzone.data.Region;
import me.bristermitten.warzone.data.WorldAngledPoint;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record ArenasConfig(
        Map<String, ArenaConfig> arenas,
        @SerializedName("lobby-spawnpoint") WorldAngledPoint lobbySpawnpoint,
        @SerializedName("pre-game-lobby-spawnpoint") WorldAngledPoint preGameLobbySpawnpoint
) {
    public static final Configuration<ArenasConfig> CONFIG = new Configuration<>(ArenasConfig.class, "arenas.yml");

    public record ArenaConfig(String world,
                              @Nullable String permission,
                              @Nullable Integer priority,
                              @SerializedName("gulag") GulagConfig gulagConfig,
                              @SerializedName("playable-area") Region playableArea,
                              @SerializedName("loot") String lootTable,
                              GameConfiguration game
    ) {
        public record GameConfiguration(
                @SerializedName("time-limit") int timeLimit,
                @SerializedName("player-limit") int playerLimit,
                @SerializedName("border-damage") int borderDamage,
                @SerializedName("border-damage-time") int borderDamageTime,
                @SerializedName("chest-rate") float chestRate,
                @SerializedName("max-gulag-entries") int maxGulagEntries
        ) {
        }

        public record GulagConfig(
                @SerializedName("fighting-area-1") AngledPoint fightingArea1,
                @SerializedName("fighting-area-2") AngledPoint fightingArea2,
                @SerializedName("spawn-area") AngledPoint spawnArea
        ) {
        }
    }
}
