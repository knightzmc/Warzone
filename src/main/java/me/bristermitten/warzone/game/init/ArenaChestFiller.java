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
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

@Singleton
public class ArenaChestFiller extends Task {
    private final Queue<Entry> chunksToProcess = new ArrayDeque<>();
    private final Set<ChunkPosition> loaded = new HashSet<>();
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
                .filter(chunk -> !loaded.contains(ChunkPosition.of(chunk)))
                .map(chunk -> new Entry(chunk, arena))
                .forEach(chunksToProcess::add);

        start();
    }

    public void unload(@NotNull final Arena arena) {
        var world = arena.forceGetWorld();
        BlockFinder.getLoadedChunks(world, arena.playableArea())
                .map(ChunkPosition::of)
                .forEach(loaded::remove);
    }


    public void add(Chunk chunk, @NotNull final Arena arena) {

        if (loaded.contains(ChunkPosition.of(chunk))) {
            return;
        }
        chunksToProcess.add(new Entry(chunk.getChunkSnapshot(), arena));
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (int i = 0; i < 10; i++) {
                var nextChunk = chunksToProcess.poll();
                if (nextChunk == null) {
                    break;
                }
                final var arena = nextChunk.arena();
                var world = arena.forceGetWorld();
                var chunk = nextChunk.chunk();
                world.getChunkAtAsync(chunk.getX(), chunk.getZ()).whenComplete((loadedChunk, t) -> {
                    if (t != null) {
                        throw new IllegalStateException("Could not load chunk", t);
                    }
                    filler.fill(world.getChunkAt(chunk.getX(), chunk.getZ()),
                            arena.lootTable(),
                            arena.gameConfig().chestRate(),
                            arena.gameConfig().maxChestY());
                    loaded.add(ChunkPosition.of(chunk));
                });
            }
            if (running) {
                schedule();
            }
        }, 1);
    }

    private record ChunkPosition(String world, int x, int z) {
        static ChunkPosition of(ChunkSnapshot chunk) {
            return new ChunkPosition(chunk.getWorldName(), chunk.getX(), chunk.getZ());
        }

        static ChunkPosition of(Chunk chunk) {
            return new ChunkPosition(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
        }
    }

    private record Entry(
            ChunkSnapshot chunk,
            Arena arena
    ) {
    }
}
