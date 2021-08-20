package me.bristermitten.warzone.game.init;

import io.vavr.collection.List;
import me.bristermitten.warzone.arena.ArenaManager;
import me.bristermitten.warzone.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class ChunkLoadFiller implements Listener {
    private final GameManager gameManager;
    private final ArenaManager arenaManager;
    private final ArenaChestFiller arenaChestFiller;

    @Inject
    public ChunkLoadFiller(GameManager gameManager, ArenaManager arenaManager, Plugin plugin, ArenaChestFiller arenaChestFiller) {
        this.gameManager = gameManager;
        this.arenaManager = arenaManager;
        this.arenaChestFiller = arenaChestFiller;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        arenaManager.getArenas()
                .filter(arena -> arena.playableArea().contains(event.getChunk()))
                .headOption()
                .flatMap(arena ->
                        List.ofAll(gameManager.getGames()).filter(game -> game.getArena().equals(arena))
                                .headOption())
                .peek(game -> arenaChestFiller.add(event.getChunk(), game.getArena()));
    }

}
