package me.bristermitten.warzone.leaderboard;

import me.bristermitten.warzone.player.WarzonePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

@Singleton
public class PlayerLeaderboard {
    private static final Comparator<WarzonePlayer> COMPARATOR = Comparator.comparing(WarzonePlayer::getKDR);
    private final SortedSet<WarzonePlayer> players = new ConcurrentSkipListSet<>(COMPARATOR.reversed());

    public void add(WarzonePlayer player) {
        players.remove(player);
        players.add(player);
    }

    public @Unmodifiable @NotNull SortedSet<WarzonePlayer> getPlayers() {
        return Collections.unmodifiableSortedSet(players);
    }

    public int getPosition(WarzonePlayer player) {
        add(player);
        return players.headSet(player).size();
    }
}
