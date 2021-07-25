package me.bristermitten.warzone.papi;

import io.vavr.control.Option;
import me.bristermitten.warzone.data.Ratio;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class WarzoneExpansion extends PlaceholderExpansion {
    private final Plugin plugin;
    private final PlayerStorage playerStorage;

    @Inject
    public WarzoneExpansion(Plugin plugin, PlayerStorage playerStorage) {
        this.plugin = plugin;
        this.playerStorage = playerStorage;
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

        return switch (params) {
            case "level" -> warzonePlayer.map(WarzonePlayer::getLevel).map(Object::toString).getOrElse("Not loaded yet");
            case "kdr" -> warzonePlayer.map(WarzonePlayer::getKDR).map(Ratio::format).getOrElse("Not loaded yet");
            default -> null;
        };
    }
}
