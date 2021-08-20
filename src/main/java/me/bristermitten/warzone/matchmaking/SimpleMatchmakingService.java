package me.bristermitten.warzone.matchmaking;

import com.google.inject.Singleton;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.state.GameStateChangeEvent;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;

@Singleton
public class SimpleMatchmakingService implements MatchmakingService, Listener {
    private final GameManager gameManager;
    private final Queue<Party> playersInQueue = new ArrayDeque<>();
    private final ArenaManager arenaManager;

    @Inject
    public SimpleMatchmakingService(GameManager gameManager, ArenaManager arenaManager, Plugin plugin) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onStateChange(GameStateChangeEvent event) {
        processQueue();
    }

    private void processQueue() {
        var toRemove = new LinkedList<Party>();
        for (Party party : playersInQueue) {
            Optional<Game> matching = gameManager.getGames().stream()
                    .filter(game -> arenaManager.partyCanUseArena(party, game.getArena()))
                    .filter(game -> game.getState() instanceof InLobbyState || game.getState() instanceof IdlingState)
                    .filter(Predicate.not(Game::isFull))
                    .max(Comparator.comparing(game -> game.getArena().priority()));

            if (matching.isPresent()) {
                gameManager.addToGame(matching.get(), party);
                toRemove.add(party);
            }
        }
        toRemove.forEach(this::unqueue);

        for (Party waiting : playersInQueue) {
            var applicable = arenaManager.getArenas()
                    .filter(arenaManager.arenaIsInUse().negate())
                    .filter(arena -> arenaManager.partyCanUseArena(waiting, arena))
                    .maxBy(Comparator.comparing(Arena::priority));
            if (applicable.isEmpty()) {
                continue;
            }
            gameManager.createNewGame(applicable.get(), waiting.getSize());
            processQueue();
        }

    }

    @Override
    public void queue(Party party) {
        playersInQueue.add(party);
        processQueue();
    }

    @Override
    public void unqueue(Party party) {
        playersInQueue.remove(party);
    }
}
