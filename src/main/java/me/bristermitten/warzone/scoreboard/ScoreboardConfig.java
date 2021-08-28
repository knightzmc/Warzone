package me.bristermitten.warzone.scoreboard;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

public record ScoreboardConfig(@SerializedName("in-game") ScoreboardTemplate inGame,
                               ScoreboardTemplate lobby,
                               @SerializedName("pre-game-lobby") ScoreboardTemplate preGameLobby,
                               @SerializedName("update-time") int updateTime) {
    public static final Configuration<ScoreboardConfig> CONFIG = new Configuration<>(ScoreboardConfig.class, "scoreboards.yml");
}
