package me.bristermitten.warzone.leaderboard.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.function.Function;

import io.vavr.collection.List;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import me.bristermitten.warzone.menu.Menu;
import me.bristermitten.warzone.menu.MenuAction;
import me.bristermitten.warzone.menu.MenuConfig;
import me.bristermitten.warzone.menu.MenuListener;
import me.bristermitten.warzone.menu.MenuRenderer;
import me.bristermitten.warzone.menu.MenuTemplate;
import me.bristermitten.warzone.player.WarzonePlayer;

public class LeaderboardMenu implements Menu {
    // For loading the configuration
    public static final Configuration<LeaderboardMenuConfig> CONFIG = new Configuration<>(LeaderboardMenuConfig.class,
            "menus/leaderboard.yml");

    private final PlayerLeaderboard playerLeaderboard; // The leaderboard data

    private final MenuTemplate global; // Template storing the default page layout
    private final Map<Integer, MenuTemplate> others; // Specific templates for each page. Used for page 1, which has a
    // different layout
    private final Function<WarzonePlayer, ItemStack> renderFunction; // Function to turn a player into an item, usually
    // their head
    private final MenuRenderer renderer; // Menu Renderer to use
    private final Plugin plugin; // Plugin to use for rendering and listening to events

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
        page = Math.max(page, 0); // If page number is less than 0, open the 1st page

        var pageTemplate = others.getOrDefault(page + 1, global); // page numbers start at 1

        var offset = 0; // Stores how many leaderboard entries are skipped due to being in previous
        // pages
        for (int i = 1; i < page + 1; i++) {
            var previousPage = others.getOrDefault(page, global);
            offset += previousPage.size();
        }

        var toRender = List.ofAll(playerLeaderboard.getPlayers())
                .drop(offset) // Remove items that would be on previous pages
                .take(pageTemplate.emptySlots()); // Take as many items as there are empty slots

        if (toRender.isEmpty()) { // If the leaderboard is empty, do nothing - this should never be true but we
            // should be safe
            return;
        }

        final var finalPageTemplate = pageTemplate; // copy to a final variable so we can use it in the lambda
        final var finalPage = page; // copy to a final variable so we can use it in the lambda
        var menu = renderer.render(pageTemplate, player, // Render the page template
                (message, p) -> message.replace("{page_number}", String.valueOf(finalPage + 1)), // Replacing
                // {page_number} with
                // the actual page
                // number
                (message, p) -> message.replace("{gui_opener_name}", player.getName()), // Replacing {gui_opener_name}
                // with the player's name
                (message, p) -> message.replace("{gui_opener_uuid}", player.getUniqueId().toString())); // Replacing
        // {gui_opener_uuid}
        // with the
        // player's UUID

        for (WarzonePlayer leaderboardEntry : toRender) { // For each leaderboard entry
            menu.setItem(menu.firstEmpty(), renderFunction.apply(leaderboardEntry)); // Set the first empty slot in the
            // menu to the rendered item
        }

        MenuListener listener = new MenuListener(menu, event -> { // Create a new listener to handle when a player
            // clicks in the GUI
            MenuConfig.MenuItemConfig clicked = finalPageTemplate.items().get(event.getSlot()); // Get the item that was
            // clicked
            event.setCancelled(true); // Cancel the event so the player can't take the items out
            if (clicked == null || clicked.action() == null) { // If they didn't click an actual item, or the item
                // doesn't have an action, do nothing
                return;
            }
            if (clicked.action() == MenuAction.PREVIOUS_PAGE) { // If they clicked the previous page button
                open(player, finalPage - 1); // Open the previous page
                return;
            }
            if (clicked.action() == MenuAction.NEXT_PAGE) { // If they clicked the next page button
                open(player, finalPage + 1); // Open the next page
                return;
            }
            clicked.action().execute(event); // Otherwise, execute the action's configured action
        });

        listener.register(plugin); // Register the listener so it can properly handle click events

        player.openInventory(menu); // Open the menu
    }

    @Override
    public void open(Player player) {
        open(player, 0); // Opens the first page
    }

    @Override
    public void close(Player player) {
        player.closeInventory(); // Close the player's GUI
    }
}
