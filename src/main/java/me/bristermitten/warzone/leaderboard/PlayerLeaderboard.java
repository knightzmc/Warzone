package me.bristermitten.warzone.leaderboard;

import me.bristermitten.warzone.player.WarzonePlayer;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.TreeSet;

@Singleton
public class PlayerLeaderboard {
    private static final Comparator<WarzonePlayer> COMPARATOR = Comparator.comparing(WarzonePlayer::getKDR);
    private final TreeSet<WarzonePlayer> players = new TreeSet<>(COMPARATOR.reversed());

    public void add(WarzonePlayer player) {
        players.remove(player);
        players.add(player);
    }

    public int getPosition(WarzonePlayer player) {
        add(player);
        return players.headSet(player).size();
    }
}
