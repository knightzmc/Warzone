package me.bristermitten.warzone.player;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.TreeSet;

@Singleton
public class PlayerLeaderboard {
    private static final Comparator<WarzonePlayer> COMPARATOR = Comparator.comparing(WarzonePlayer::getKDR);
    private final TreeSet<WarzonePlayer> players = new TreeSet<>(COMPARATOR);

    public void add(WarzonePlayer player) {
        players.add(player);
    }

    public int getPosition(WarzonePlayer player) {
        players.add(player);
        return players.headSet(player).size();
    }
}