package me.bristermitten.warzone.game;

import com.google.inject.assistedinject.Assisted;
import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.bossbar.game.GameBossBar;
import me.bristermitten.warzone.game.gulag.Gulag;
import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.statistic.PlayerDeath;
import me.bristermitten.warzone.game.statistic.PlayerInformation;
import me.bristermitten.warzone.game.timer.GameTimer;
import me.bristermitten.warzone.game.world.GameBorder;
import me.bristermitten.warzone.lobby.PreGameLobbyTimer;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.state.Stateful;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game implements Stateful<Game, GameState> {
    private final UUID uuid;
    private final Arena arena;
    private final Set<Party> players;
    private final PartySize acceptedSize;
    private final GameTimer timer;
    private final Map<UUID, PlayerInformation> playerInformationMap = new HashMap<>();
    private final Set<PlayerDeath> deaths = new HashSet<>();
    private final Gulag gulag;
    private final GameBorder gameBorder;
    private final GameBossBar gameBossBar;
    private final PreGameLobbyTimer preGameLobbyTimer;
    private GameState state = IdlingState.INSTANCE;

    @Inject
    Game(@Assisted Arena arena, @Assisted Set<Party> players, @Assisted PartySize acceptedSize, PreGameLobbyTimer preGameLobbyTimer) {
        this.uuid = UUID.randomUUID();
        this.arena = arena;
        this.preGameLobbyTimer = preGameLobbyTimer;
        preGameLobbyTimer.bind(this);
        this.players = new HashSet<>(players);
        this.acceptedSize = acceptedSize;
        this.timer = new GameTimer(TimeUnit.SECONDS.toMillis(arena.gameConfig().timeLimit()));
        this.gulag = new Gulag(this);
        this.gameBorder = new GameBorder(arena);
        this.gameBossBar = new GameBossBar(this);

        players.stream()
                .map(Party::getAllMembers)
                .flatMap(Collection::stream)
                .forEach(playerUUID -> playerInformationMap.put(playerUUID, new PlayerInformation(playerUUID)));
    }

    public void recordDeath(PlayerDeath playerDeath) {
        deaths.add(playerDeath);
    }


    public GameBorder getGameBorder() {
        return gameBorder;
    }

    public GameBossBar getGameBossBar() {
        return gameBossBar;
    }

    public GameTimer getTimer() {
        return timer;
    }

    public Arena getArena() {
        return arena;
    }

    Set<Party> getParties() {
        return players;
    }

    public UUID getUuid() {
        return uuid;
    }

    public PreGameLobbyTimer getPreGameLobbyTimer() {
        return preGameLobbyTimer;
    }

    Map<UUID, PlayerInformation> getPlayerInformationMap() {
        return playerInformationMap;
    }

    public Option<PlayerInformation> getInfo(UUID uuid) {
        return Option.of(playerInformationMap.get(uuid));
    }

    public io.vavr.collection.Set<Party> getPartiesInGame() {
        return io.vavr.collection.HashSet.ofAll(players);
    }

    public GameState getState() {
        return state;
    }

    @Override
    public void setCurrentState(@NotNull GameState currentState) {
        this.state.onLeave(this);
        this.state = currentState;
        this.state.onEnter(this);
    }

    public PartySize getAcceptedSize() {
        return acceptedSize;
    }


    public boolean isFull() {
        var currentSize = acceptedSize.getSize() * players.size();
        return currentSize >= arena.gameConfig().playerLimit();
    }

    public Gulag getGulag() {
        return gulag;
    }

    public @Unmodifiable Set<PlayerDeath> getDeaths() {
        return Set.copyOf(deaths);
    }

    @Override
    public String toString() {
        return "Game{" +
               "uuid=" + uuid +
               ", arena=" + arena +
               ", players=" + players +
               ", acceptedSize=" + acceptedSize +
               ", timer=" + timer +
               ", playerInformationMap=" + playerInformationMap +
               ", deaths=" + deaths +
               ", gulag=" + gulag +
               ", gameBorder=" + gameBorder +
               ", gameBossBar=" + gameBossBar +
               ", preGameLobbyTimer=" + preGameLobbyTimer +
               ", state=" + state +
               '}';
    }
}
