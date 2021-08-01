package me.bristermitten.warzone.leaderboard;

import com.google.gson.annotations.SerializedName;
import me.bristermitten.warzone.menu.MenuConfig;

public record LeaderboardMenuConfig(
        @SerializedName("leaderboard-entry") MenuConfig.PageConfig.ItemConfig leaderboardEntry,
        MenuConfig menu
) {
}
