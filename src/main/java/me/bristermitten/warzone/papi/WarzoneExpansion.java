package me.bristermitten.warzone.papi;

import io.vavr.control.Option;
import me.bristermitten.warzone.data.Ratio;
import me.bristermitten.warzone.player.PlayerLeaderboard;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.bristermitten.warzone.player.xp.XPHandler;
import me.bristermitten.warzone.util.OrdinalFormatter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Set;

public class WarzoneExpansion extends PlaceholderExpansion {
    public static final String NOT_LOADED_YET = "Not loaded yet";
    private final Plugin plugin;
    private final PlayerStorage playerStorage;
    private final XPHandler xpHandler;

    private final PlayerLeaderboard leaderboard;

    private final Set<WarzonePlaceholder> extraPlaceholders;

    @Inject
    public WarzoneExpansion(Plugin plugin, PlayerStorage playerStorage, XPHandler xpHandler, PlayerLeaderboard leaderboard, Set<WarzonePlaceholder> extraPlaceholders) {
        this.plugin = plugin;
        this.playerStorage = playerStorage;
        this.xpHandler = xpHandler;
        this.leaderboard = leaderboard;
        this.extraPlaceholders = extraPlaceholders;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "warzone";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        var warzonePlayer = Option.of(playerStorage.fetch(player.getUniqueId()))
                // While we obviously can't use the loaded player yet, we can trigger a query so that it starts
                .onEmpty(() -> playerStorage.load(player.getUniqueId()));

        var simple = switch (params) {
            case "level" -> warzonePlayer.map(WarzonePlayer::getLevel).map(Object::toString).getOrElse(NOT_LOADED_YET);
            case "kdr" -> warzonePlayer.map(WarzonePlayer::getKDR).map(Ratio::format).getOrElse(NOT_LOADED_YET);
            case "wlr" -> warzonePlayer
                    .map(WarzonePlayer::getWLR)
                    .map(Ratio::format)
                    .getOrElse(NOT_LOADED_YET);
            case "global_ranking" -> warzonePlayer
                    .map(leaderboard::getPosition)
                    .map(i -> i + 1 /* human readable */)
                    .map(OrdinalFormatter::format)
                    .getOrElse(NOT_LOADED_YET);
            case "xp_required" -> warzonePlayer
                    .map(player1 -> xpHandler.xpRequiredForLevel(player1.getLevel() + 1))
                    .map(Object::toString)
                    .getOrElse(NOT_LOADED_YET);
            default -> null;
        };
        if (simple != null) {
            return simple;
        }

        var firstMatching = extraPlaceholders
                .stream()
                .filter(p -> p.getPattern().matcher(params).matches())
                .findFirst();
        if (firstMatching.isEmpty()) {
            return null;
        }
        return firstMatching.get().onPlaceholderRequest(player, params);
    }
}
