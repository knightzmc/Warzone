package me.bristermitten.warzone.leaderboard;

import com.google.inject.AbstractModule;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenu;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenuLoader;

public class LeaderboardModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LeaderboardMenu.class).toProvider(LeaderboardMenuLoader.class);
    }
}
