package me.bristermitten.warzone.player.xp;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record XPConfig(@SerializedName("level-algorithm") String levelAlgorithm,
                       int kill,
                       int win,
                       @SerializedName("top-3") int top3,
                       @SerializedName("xp-gain-sound") @Nullable Sound xpGainSound,
                       @SerializedName("level-up-sound") @Nullable Sound levelUpSound) {
    public static final @NotNull Configuration<XPConfig> CONFIG = new Configuration<>(XPConfig.class, "xp.yml");
}
