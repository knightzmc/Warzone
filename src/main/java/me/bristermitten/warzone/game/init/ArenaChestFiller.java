package me.bristermitten.warzone.game.init;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayDeque;
import java.util.Queue;

@Singleton
public class ArenaChestFiller extends Task {
    private final Queue<Entry> chunksToProcess = new ArrayDeque<>();
    private final Plugin plugin;
    private final ChunkChestFiller filler;

    @Inject
    public ArenaChestFiller(Plugin plugin, ChunkChestFiller filler) {
        this.plugin = plugin;
        this.filler = filler;
    }

    public void add(@NotNull final Arena arena) {
        var world = arena.forceGetWorld();
        BlockFinder.getLoadedChunks(world, arena.playableArea())
                .map(chunk -> new Entry(chunk, arena))
                .forEach(chunksToProcess::add);

        start();
    }


    public void add(Chunk chunk, @NotNull final Arena arena) {
        chunksToProcess.add(new Entry(chunk.getChunkSnapshot(), arena));
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            var next = chunksToProcess.poll();
            if (next == null) {
                return;
            }
            var world = next.arena().forceGetWorld();
            var chunk = next.chunk();
            if (world.isChunkLoaded(chunk.getX(), chunk.getZ())) {
                filler.fill(world.getChunkAt(chunk.getX(), chunk.getZ()), next.arena().lootTable(), next.arena().gameConfig().chestRate());
            }
            if (running) {
                schedule();
            }
        }, 5);
    }

    private record Entry(
            ChunkSnapshot chunk,
            Arena arena
    ) {
    }
}
