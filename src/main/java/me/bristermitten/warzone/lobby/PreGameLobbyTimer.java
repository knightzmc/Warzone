package me.bristermitten.warzone.lobby;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class PreGameLobbyTimer {
    private final Provider<GameConfig> configProvider;
    private final List<Runnable> onComplete = new LinkedList<>();
    private final PreGameLobbyBossBar bossBar;
    private final Schedule schedule;
    private Game managing;
    private long startTime;
    private long endTime;
    private long duration;

    @Inject
    public PreGameLobbyTimer(Provider<GameConfig> configProvider, Schedule schedule) {
        this.configProvider = configProvider;
        bossBar = new PreGameLobbyBossBar(this, configProvider);
        this.schedule = schedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PreGameLobbyTimer that)) return false;
        return startTime == that.startTime && endTime == that.endTime && getDuration() == that.getDuration() && managing.equals(that.managing);
    }

    public void addCompletionHook(Runnable runnable) {
        onComplete.add(runnable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(managing, startTime, endTime, getDuration());
    }

    public void bind(Game game) {
        if (this.managing != null) {
            throw new IllegalStateException("Already bound to " + managing);
        }
        this.managing = game;
    }

    /**
     * Checks if the managed game has enough players to start the timer
     */
    public boolean canStart() {
        var maxPlayers = managing.getArena().gameConfig().playerLimit();
        var playerCount = managing.getPartiesInGame()
                .flatMap(Party::getAllMembers)
                .filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .length();
        var percentageThreshold = configProvider.get().gameStartTimerConfig().threshold();
        return playerCount >= percentageThreshold * maxPlayers;
    }

    public void start() {
        if (startTime != 0) {
            return; // already started
        }
        if (!canStart()) {
            return;
        }
        this.startTime = System.currentTimeMillis();
        this.endTime = startTime + configProvider.get().gameStartTimerConfig().lengthMillis();
        this.duration = endTime - startTime;

        schedule.runLater(duration, () -> onComplete.forEach(Runnable::run));
    }

    public long getDuration() {
        return duration;
    }

    public long getTimeRemaining() {
        return endTime - System.currentTimeMillis();
    }

    public PreGameLobbyBossBar getBossBar() {
        return bossBar;
    }
}
