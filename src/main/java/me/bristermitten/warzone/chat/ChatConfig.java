package me.bristermitten.warzone.chat;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.config.Configuration;

import java.util.List;

public record ChatConfig(
        Formats formats,
        @SerializedName("stats-hover-message") List<String> statsHoverMessage
) {
    public static final Configuration<ChatConfig> CONFIG = new Configuration<>(ChatConfig.class, "chat.yml");

    public record Formats(@SerializedName("in-game") String inGame,
                          @SerializedName("pre-game-lobby") String preGameLobby,
                          @SerializedName("lobby") String lobby) {

    }
}
