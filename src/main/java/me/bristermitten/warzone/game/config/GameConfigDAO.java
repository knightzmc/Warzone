package me.bristermitten.warzone.game.config;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.bossbar.BossBarConfigDAO;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.game.spawning.PlayerSpawningMethod;
import me.bristermitten.warzone.game.spawning.bus.BattleBusConfig;
import me.bristermitten.warzone.item.ItemConfig;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record GameConfigDAO(@SerializedName("spectator-mode") SpectatorConfigDAO spectatorMode,
                            @SerializedName("drop-in-method") @Nullable PlayerSpawningMethod playerSpawningMethod,
                            @SerializedName("battle-bus") BattleBusConfig battleBusConfig,
                            @SerializedName("game-start-timer") GameStartTimerConfigDAO gameStartTimerConfigDAO,
                            @SerializedName("game-end-timer") @Nullable Integer gameEndTimer) {
    public static final Configuration<GameConfigDAO> CONFIG = new Configuration<>(GameConfigDAO.class, "game.yml");

    record GameStartTimerConfigDAO(
            double threshold,
            int length,
            @SerializedName("boss-bar") BossBarConfigDAO bossBar
    ) {
    }

    record SpectatorConfigDAO(
            @SerializedName("allow-flight") Boolean allowFlight,
            @SerializedName("invisible") Boolean invisible,
            @SerializedName("potion-effects") List<PotionEffectDAO> potionEffects,
            @SerializedName("items") Map<Integer, SpectatorModeHotbarItemDAO> items
    ) {
        record SpectatorModeHotbarItemDAO(
                ItemConfig item,
                SpectatorModeHotbarItemAction action
        ) {
        }

    }

    record PotionEffectDAO(
            PotionEffectType type,
            int amplifier
    ) {
    }
}
