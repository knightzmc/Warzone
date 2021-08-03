package me.bristermitten.warzone.leaderboard.menu;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import me.bristermitten.warzone.menu.ItemRenderer;
import me.bristermitten.warzone.menu.MenuConfigLoader;
import me.bristermitten.warzone.menu.MenuRenderer;
import me.bristermitten.warzone.menu.MenuTemplate;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashMap;
import java.util.function.Function;

public class LeaderboardMenuLoader implements Provider<LeaderboardMenu> {
    private final PlayerLeaderboard playerLeaderboard;
    private final Provider<LeaderboardMenuConfig> configProvider;
    private final MenuConfigLoader configLoader;
    private final ItemRenderer renderer;
    private final ChatFormatter formatter;
    private final MenuRenderer menuRenderer;
    private final Plugin plugin;

    @Inject
    public LeaderboardMenuLoader(PlayerLeaderboard playerLeaderboard,
                                 Provider<LeaderboardMenuConfig> configProvider,
                                 MenuConfigLoader configLoader,
                                 ItemRenderer renderer,
                                 ChatFormatter formatter,
                                 MenuRenderer menuRenderer,
                                 Plugin plugin) {
        this.playerLeaderboard = playerLeaderboard;
        this.configProvider = configProvider;
        this.configLoader = configLoader;
        this.renderer = renderer;
        this.formatter = formatter;
        this.menuRenderer = menuRenderer;
        this.plugin = plugin;
    }

    @Override
    public @NotNull LeaderboardMenu get() {
        var leaderboardMenuConfig = configProvider.get();
        var globalPage = leaderboardMenuConfig.pages().get("global");
        if (globalPage == null) {
            throw new IllegalArgumentException("Leaderboard menu MUST have a global page, otherwise we can't generate pages dynamically");
        }
        var others = new HashMap<Integer, MenuTemplate>();
        Function<WarzonePlayer, ItemStack> renderFunction = player -> {
            var config = configProvider.get();
            var head = config.leaderboardEntry();
            return renderer.render(head, formatter, player.getOfflinePlayer());
        };
        return new LeaderboardMenu(playerLeaderboard, configLoader.load(globalPage), others, renderFunction, menuRenderer, plugin);
    }
}
