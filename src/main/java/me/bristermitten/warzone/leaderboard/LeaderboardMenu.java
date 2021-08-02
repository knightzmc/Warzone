package me.bristermitten.warzone.leaderboard;

import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.menu.Menu;
import me.bristermitten.warzone.menu.MenuLoader;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import javax.inject.Provider;

public class LeaderboardMenu {
    public static final Configuration<LeaderboardMenuConfig> CONFIG = new Configuration<>(LeaderboardMenuConfig.class, "menus/leaderboard.yml");
    private final MenuLoader loader;
    private final Provider<LeaderboardMenuConfig> configProvider;
    private final PlayerLeaderboard leaderboard;


    @Inject
    public LeaderboardMenu(MenuLoader loader, Provider<LeaderboardMenuConfig> configProvider, PlayerLeaderboard leaderboard) {
        this.loader = loader;
        this.configProvider = configProvider;
        this.leaderboard = leaderboard;
    }


    public void open(Player player) {
        LeaderboardMenuConfig menuConfig = configProvider.get();
        Menu menu = loader.load(player, menuConfig.menu());
        for (WarzonePlayer leaderboardPlayer : leaderboard.getPlayers()) {
            menu.add(
                    loader.loadItem(leaderboardPlayer.getOfflinePlayer(), menuConfig.leaderboardEntry())
            );
        }
        menu.open(player);
    }
}
