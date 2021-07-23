package me.bristermitten.warzone.scoreboard;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record ScoreboardConfig(@Unmodifiable @SerializedName("in-game") List<String> inGame,
                               @Unmodifiable List<String> lobby) {
    public static final Configuration<ScoreboardConfig> CONFIG = new Configuration<>(ScoreboardConfig.class, "scoreboards.yml");
}
