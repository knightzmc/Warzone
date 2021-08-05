package me.bristermitten.warzone.leaderboard.menu;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.item.ItemConfig;
import me.bristermitten.warzone.menu.MenuConfig;

import java.util.Map;

public record LeaderboardMenuConfig(
        @SerializedName("leaderboard-entry") ItemConfig leaderboardEntry,
        Map<String, MenuConfig> pages
) {
}
