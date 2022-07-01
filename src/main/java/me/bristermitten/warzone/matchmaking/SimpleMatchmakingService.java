package me.bristermitten.warzone.matchmaking;

import com.google.inject.Singleton;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Predicate;

import javax.inject.Inject;

import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.GameJoinLeaveService;
import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.game.state.GameStateChangeEvent;
import me.bristermitten.warzone.game.state.IdlingState;
import me.bristermitten.warzone.game.state.InLobbyState;
import me.bristermitten.warzone.party.Party;

@Singleton
public class SimpleMatchmakingService implements MatchmakingService, Listener {
    private final GameRepository gameRepository;
    private final GameManager gameManager;
    private final GameJoinLeaveService gameJoinLeaveService;
    private final Queue<Party> playersInQueue = new ArrayDeque<>();
    private final ArenaManager arenaManager;

    @Inject
    public SimpleMatchmakingService(GameRepository gameRepository, ArenaManager arenaManager, Plugin plugin, GameManager gameManager, GameJoinLeaveService gameJoinLeaveService) {
        this.gameRepository = gameRepository;
        this.arenaManager = arenaManager;
        this.gameManager = gameManager;
        this.gameJoinLeaveService = gameJoinLeaveService;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onStateChange(GameStateChangeEvent event) {
        processQueue();
    }

    private void processQueue() {
        var toRemove = new LinkedList<Party>();
        // Firstly, we go through all the already existing, waiting games and see if any of them fit the requirements
        for (Party party : playersInQueue) {
            Option<Game> matching = gameRepository.getGames()
                    .filter(game -> arenaManager.partyCanUseArena(party, game.getArena()))
                    .filter(game -> game.getState() instanceof InLobbyState || game.getState() instanceof IdlingState)
                    .filter(Predicate.not(Game::isFull))
                    .maxBy(Comparator.comparing(game -> game.getArena().priority()));

            if (matching.isDefined()) { // if any game matches, make the party join the game
                gameJoinLeaveService.join(matching.get(), party);
                toRemove.add(party);
            }
        }
        // remove all of the parties that have now been queued
        toRemove.forEach(this::unqueue);

        // For all of the parties that are still waiting, check if there are any free arenas that we can create a game in
        boolean createdNewGame = false;
        for (Party waiting : playersInQueue) {
            var applicable = arenaManager.getArenas()
                    .filter(arenaManager.arenaIsInUse().negate())
                    .filter(arena -> arenaManager.partyCanUseArena(waiting, arena))
                    .maxBy(Comparator.comparing(Arena::priority));
            if (applicable.isEmpty()) { // No free arenas, keep the players waiting
                continue;
            }
            gameManager.createNewGame(applicable.get(), waiting.getSize()); // Create and register the new game
            /*
             Once 1 new game is created, we break out of the loop, and re-distribute all the waiting players
             This is because we want to be as conservative as possible with game creation, since the amount of
             arenas is finite.
             Let's say we did 1 iteration, and create a new game for that party (A). Party B can also go in party A's
             arena, but if we kept looping, it would create a new game for them (as the arena for party A would
             now be full)
            */
            createdNewGame = true;
            break;
        }
        if (createdNewGame) {
            processQueue(); // and then go back to the start
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
