package me.bristermitten.warzone.papi;

import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import me.bristermitten.warzone.leaderboard.PlayerLeaderboard;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.regex.Pattern;

public class LeaderboardPlaceholder implements WarzonePlaceholder {

    private static final Pattern LEADERBOARD_PATTERN = Pattern.compile("leaderboard_(name|kdr)_([1-9]\\d*)");
    private final PlayerLeaderboard leaderboard;

    @Inject
    public LeaderboardPlaceholder(PlayerLeaderboard leaderboard) {
        this.leaderboard = leaderboard;
    }

    @Override

    public @Nullable String onPlaceholderRequest(@Nullable OfflinePlayer offlinePlayer, @NotNull String params) {
        var matcher = LEADERBOARD_PATTERN.matcher(params);
        if (!matcher.find()) {
            return null;
        }
        var type = matcher.group(1);
        var indexString = matcher.group(2);
        //noinspection UnstableApiUsage
        var index = Ints.tryParse(indexString);
        if (index == null) {
            return null;
        }
        index -= 1; // make it 0 indexed
        if (index < 0 || index >= leaderboard.getPlayers().size()) {
            return null;
        }
        return switch (type) {
            case "name" -> Iterables.get(leaderboard.getPlayers(), index).getOfflinePlayer().getName();
            case "kdr" -> Iterables.get(leaderboard.getPlayers(), index).getKDR().format();
            default -> null;
        };
    }
}
