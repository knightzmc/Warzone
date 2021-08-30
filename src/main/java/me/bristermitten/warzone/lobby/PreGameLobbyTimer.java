package me.bristermitten.warzone.lobby;

import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.config.GameConfig;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.timer.Timer;
import me.bristermitten.warzone.util.Schedule;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.LinkedList;
import java.util.List;

public class PreGameLobbyTimer extends Timer {
    private final Provider<GameConfig> configProvider;
    private final List<Runnable> onComplete = new LinkedList<>();
    private final PreGameLobbyBossBar bossBar;
    private final Schedule schedule;
    private Game managing;

    @Inject
    public PreGameLobbyTimer(Provider<GameConfig> configProvider, Schedule schedule) {
        this.configProvider = configProvider;
        bossBar = new PreGameLobbyBossBar(this, configProvider);
        this.schedule = schedule;
        this.durationMillis = loadDuration();
    }


    public void addCompletionHook(Runnable runnable) {
        onComplete.add(runnable);
    }

    public void bind(Game game) {
        if (this.managing != null) {
            throw new IllegalStateException("Already bound to " + managing);
        }
        this.managing = game;
    }

    private long loadDuration() {
        return configProvider.get().gameStartTimerConfig().lengthMillis();
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

    @Override
    public long getTimeRemaining() {
        if (!hasStarted()) {
            return durationMillis;
        }
        return super.getTimeRemaining();
    }

    public void forceStart() {
        forceStart(loadDuration());
    }

    public void forceStart(long duration) {
        this.durationMillis = duration;
        super.start();
        schedule.runLater(durationMillis, () -> onComplete.forEach(Runnable::run));
    }

    @Override
    public void start() {
        if (hasStarted()) {
            return; // already started
        }
        if (!canStart()) {
            return;
        }
        forceStart();

    }

    public PreGameLobbyBossBar getBossBar() {
        return bossBar;
    }
}
