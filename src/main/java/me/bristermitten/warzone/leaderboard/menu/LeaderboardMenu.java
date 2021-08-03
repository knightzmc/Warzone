package me.bristermitten.warzone.leaderboard.menu;

import io.vavr.collection.List;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import me.bristermitten.warzone.menu.*;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.function.Function;

public class LeaderboardMenu implements Menu {
    public static final Configuration<LeaderboardMenuConfig> CONFIG = new Configuration<>(LeaderboardMenuConfig.class, "menus/leaderboard.yml");


    private final PlayerLeaderboard playerLeaderboard;

    private final MenuTemplate global;
    private final Map<Integer, MenuTemplate> others;
    private final Function<WarzonePlayer, ItemStack> renderFunction;
    private final MenuRenderer renderer;
    private final Plugin plugin;

    public LeaderboardMenu(PlayerLeaderboard playerLeaderboard,
                           MenuTemplate global,
                           Map<Integer, MenuTemplate> others,
                           Function<WarzonePlayer, ItemStack> renderFunction,
                           MenuRenderer renderer, Plugin plugin) {
        this.playerLeaderboard = playerLeaderboard;
        this.global = global;
        this.others = others;
        this.renderFunction = renderFunction;
        this.renderer = renderer;
        this.plugin = plugin;
    }

    public void open(Player player, int page) {
        page = Math.max(page, 0);

        var pageTemplate = others.get(page + 1); // pages are one indexed
        if (pageTemplate == null) {
            pageTemplate = global;
        }

        var offset = 0;
        for (int i = 1; i < page + 1; i++) {
            var previousPage = others.getOrDefault(page, global);
            offset += previousPage.size();
        }

        var toRender = List.ofAll(playerLeaderboard.getPlayers()).drop(offset)
                .take(pageTemplate.emptySlots());

        if (toRender.isEmpty()) {
            return;
        }

        final var finalPageTemplate = pageTemplate;
        final var finalPage = page;
        var menu = renderer.render(pageTemplate, player,
                (message, p) -> message.replace("{page_number}", String.valueOf(finalPage + 1)),
                (message, p) -> message.replace("{gui_opener_name}", player.getName()),
                (message, p) -> message.replace("{gui_opener_uuid}", player.getUniqueId().toString()));

        for (WarzonePlayer leaderboardEntry : toRender) {
            menu.setItem(menu.firstEmpty(), renderFunction.apply(leaderboardEntry));
        }

        new MenuListener(menu, event -> {
            MenuConfig.ItemConfig clicked = finalPageTemplate.items().get(event.getSlot());
            event.setCancelled(true);
            if (clicked == null || clicked.action() == null) {
                return;
            }
            if (clicked.action() == MenuAction.PREVIOUS_PAGE) {
                open(player, finalPage - 1);
                return;
            }
            if (clicked.action() == MenuAction.NEXT_PAGE) {
                open(player, finalPage + 1);
                return;
            }
            clicked.action().execute(event);
        }).register(plugin);

        player.openInventory(menu);
    }

    @Override
    public void open(Player player) {
        open(player, 0);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
    }
}
