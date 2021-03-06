package me.bristermitten.warzone.arena;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.bossbar.BossBarConfigDAO;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.data.AngledPoint;
import me.bristermitten.warzone.data.Region;
import me.bristermitten.warzone.data.WorldAngledPoint;
import org.jetbrains.annotations.Nullable;

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
                @SerializedName("min-border-size") @Nullable Double minBorderSize,
                @SerializedName("border-damage-time") int borderDamageTime,
                @SerializedName("chest-rate") float chestRate,
                @SerializedName("max-gulag-entries") @Nullable Integer maxGulagEntries,
                @SerializedName("boss-bar") BossBarConfigDAO bossBarConfigDAO,
                @SerializedName("max-chest-y") @Nullable Integer maxChestY,
                @SerializedName("battle-bus-speed") @Nullable Long battleBusSpeed
        ) {

        }

        record GulagConfigDAO(
                @SerializedName("fighting-area-1") AngledPoint fightingArea1,
                @SerializedName("fighting-area-2") AngledPoint fightingArea2,
                @SerializedName("spawn-area") AngledPoint spawnArea
        ) {
        }
    }
}
