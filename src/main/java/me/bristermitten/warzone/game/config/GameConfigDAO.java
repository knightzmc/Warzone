package me.bristermitten.warzone.game.config;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.item.ItemConfig;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;

public record GameConfigDAO(@SerializedName("spectator-mode") SpectatorConfigDAO spectatorMode) {
    public static final Configuration<GameConfigDAO> CONFIG = new Configuration<>(GameConfigDAO.class, "game.yml");

    public record SpectatorConfigDAO(
            @SerializedName("allow-flight") Boolean allowFlight,
            @SerializedName("invisible") Boolean invisible,
            @SerializedName("potion-effects") List<PotionEffectDAO> potionEffects,
            @SerializedName("items") Map<Integer, SpectatorModeHotbarItemDAO> items
    ) {
        public record SpectatorModeHotbarItemDAO(
                ItemConfig item,
                SpectatorModeHotbarItemAction action
        ) {
        }

    }

    public record PotionEffectDAO(
            PotionEffectType type,
            int amplifier
    ) {
    }
}
