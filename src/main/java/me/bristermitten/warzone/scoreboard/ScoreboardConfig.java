package me.bristermitten.warzone.scoreboard;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import org.jetbrains.annotations.Unmodifiable;

public record ScoreboardConfig(@Unmodifiable @SerializedName("in-game") ScoreboardTemplate inGame,
                               @Unmodifiable ScoreboardTemplate lobby) {
    public static final Configuration<ScoreboardConfig> CONFIG = new Configuration<>(ScoreboardConfig.class, "scoreboards.yml");
}
