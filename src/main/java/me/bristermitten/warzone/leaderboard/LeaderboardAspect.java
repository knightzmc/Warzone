package me.bristermitten.warzone.leaderboard;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenu;
import me.bristermitten.warzone.leaderboard.menu.LeaderboardMenuLoader;

public class LeaderboardAspect implements Aspect {
    @Override
    public Module generateModule() throws IllegalStateException {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bind(LeaderboardMenu.class).toProvider(LeaderboardMenuLoader.class);
            }
        };
    }
}
