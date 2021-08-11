package me.bristermitten.warzone.game.init;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
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
        var world = arena.getWorld().getOrElseThrow(() -> new IllegalStateException("Could not get world named " + arena.getWorld()));
        BlockFinder.getChunks(world, arena.playableArea())
                .stream()
                .map(chunk -> new Entry(chunk, arena))
                .forEach(chunksToProcess::add);

        start();
    }


    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            var next = chunksToProcess.poll();
            if (next == null) {
                return;
            }

            filler.fill(next.chunk(), next.arena().lootTable(), next.arena().gameConfiguration().chestRate());
            if (running) {
                schedule();
            }
        }, 8);
    }

    private record Entry(
            ChunkSnapshot chunk,
            Arena arena
    ) {
    }
}
