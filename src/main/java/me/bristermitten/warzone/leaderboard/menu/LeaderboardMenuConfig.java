package me.bristermitten.warzone.leaderboard.menu;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.menu.MenuConfig;

import java.util.Map;

public record LeaderboardMenuConfig(
        @SerializedName("leaderboard-entry") MenuConfig.ItemConfig leaderboardEntry,
        Map<String, MenuConfig> pages
) {
}
