package me.bristermitten.warzone.game;

import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.game.gulag.Gulag;
import me.bristermitten.warzone.game.state.GameState;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartySize;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.state.Stateful;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game implements Stateful<Game, GameState> {
    private final Arena arena;
    private final Set<Party> players;
    private final PartySize acceptedSize;
    private final GameTimer timer;
    private final Map<UUID, PlayerInformation> playerInformationMap = new HashMap<>();
    private GameState state = IdlingState.INSTANCE;
    private final Gulag gulag;

    public Game(Arena arena, Set<Party> players, PartySize acceptedSize) {
        this.arena = arena;
        this.players = new HashSet<>(players);
        this.acceptedSize = acceptedSize;
        this.timer = new GameTimer(TimeUnit.SECONDS.toMillis(arena.gameConfiguration().timeLimit()));
        this.gulag = new Gulag(this);
        players.forEach(party -> party.getAllMembers().forEach(uuid -> playerInformationMap.put(uuid, new PlayerInformation(uuid))));
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


    Map<UUID, PlayerInformation> getPlayerInformationMap() {
        return playerInformationMap;
    }

    public Option<PlayerInformation> getInfo(UUID uuid) {
        return Option.of(playerInformationMap.get(uuid));
    }

    public @Unmodifiable Set<Party> getPartiesInGame() {
        return Set.copyOf(players);
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
        return currentSize >= arena.gameConfiguration().playerLimit();
    }

    public Gulag getGulag() {
        return gulag;
    }
}
