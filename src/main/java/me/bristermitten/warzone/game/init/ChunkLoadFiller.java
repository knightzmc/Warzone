package me.bristermitten.warzone.game.init;

import io.vavr.collection.List;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.repository.GameRepository;
import me.bristermitten.warzone.listener.EventListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;

import javax.inject.Inject;

public class ChunkLoadFiller implements EventListener {
    private final GameRepository gameRepository;
    private final ArenaManager arenaManager;
    private final ArenaChestFiller arenaChestFiller;

    @Inject
    public ChunkLoadFiller(GameRepository gameRepository, ArenaManager arenaManager, ArenaChestFiller arenaChestFiller) {
        this.gameRepository = gameRepository;
        this.arenaManager = arenaManager;
        this.arenaChestFiller = arenaChestFiller;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        arenaManager.getArenas()
                .filter(arena -> arena.world().equals(event.getChunk().getWorld().getName()))
                .filter(arena -> arena.playableArea().contains(event.getChunk()))
                .headOption()
                .flatMap(arena -> List.ofAll(gameRepository.getGames())
                        .filter(game -> game.getArena().equals(arena))
                        .headOption())
                .peek(game -> arenaChestFiller.add(event.getChunk(), game.getArena()));
    }

}
