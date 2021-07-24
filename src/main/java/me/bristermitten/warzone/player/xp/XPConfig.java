package me.bristermitten.warzone.player.xp;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

public record XPConfig(@SerializedName("level-algorithm") String levelAlgorithm,
                       int kill,
                       int win,
                       @SerializedName("top-3") int top3) {
    public static Configuration<XPConfig> CONFIG = new Configuration<>(XPConfig.class, "xp.yml");
}
