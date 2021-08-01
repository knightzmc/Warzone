package me.bristermitten.warzone.leaderboard;

import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.menu.Menu;

public class LeaderboardMenu {
    public static final Configuration<LeaderboardMenuConfig> CONFIG = new Configuration<>(LeaderboardMenuConfig.class, "menus/leaderboard.yml");
    private final PlayerLeaderboard playerLeaderboard;
    private final Menu menu;

    public LeaderboardMenu(Menu menu, PlayerLeaderboard playerLeaderboard) {
        this.menu = menu;
        this.playerLeaderboard = playerLeaderboard;
    }
}
