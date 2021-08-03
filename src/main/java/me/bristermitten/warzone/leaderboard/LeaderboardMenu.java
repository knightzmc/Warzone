package me.bristermitten.warzone.leaderboard;

import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.menu.Menu;
import me.bristermitten.warzone.menu.MenuLoader;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;

public class LeaderboardMenu {
    public static final Configuration<LeaderboardMenuConfig> CONFIG = new Configuration<>(LeaderboardMenuConfig.class, "menus/leaderboard.yml");
    private final MenuLoader loader;
    private final Provider<LeaderboardMenuConfig> configProvider;
    private final PlayerLeaderboard leaderboard;
    private final Plugin plugin;


    @Inject
    public LeaderboardMenu(MenuLoader loader, Provider<LeaderboardMenuConfig> configProvider, PlayerLeaderboard leaderboard, Plugin plugin) {
        this.loader = loader;
        this.configProvider = configProvider;
        this.leaderboard = leaderboard;
        this.plugin = plugin;
    }


    public void open(Player player) {
        LeaderboardMenuConfig menuConfig = configProvider.get();
        Menu menu = loader.load(player, menuConfig.menu());
        for (WarzonePlayer leaderboardPlayer : leaderboard.getPlayers()) {
            var page = menu.firstEmptyPage(plugin);
            var pageNumber = menu.pages().indexOf(page);
            var formatter = loader.generateFormatter(leaderboardPlayer.getOfflinePlayer(), String.valueOf(pageNumber));
            menu.addFirstEmpty(loader.loadItem(leaderboardPlayer.getOfflinePlayer(), menuConfig.leaderboardEntry(), formatter));
        }
        menu.open(player);
    }
}
