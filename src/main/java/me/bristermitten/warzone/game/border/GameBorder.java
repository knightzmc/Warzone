package me.bristermitten.warzone.game.border;

import io.vavr.collection.List;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.Plugin;

public class GameBorder extends Task {
    private final WorldBorder border;
    private final World world;
    private final Arena around;
    private final Plugin plugin;

    public GameBorder(Arena around, Plugin plugin) {
        this.world = around.getWorld().getOrElseThrow(() -> new IllegalStateException("Could not load world for arena " + around));
        this.border = world.getWorldBorder();
        this.around = around;
        this.plugin = plugin;
    }

    private void damagePlayersInBorder() {
        List.ofAll(Bukkit.getOnlinePlayers())
                .filter(player -> player.getWorld().equals(world))
                .filter(player -> !border.isInside(player.getLocation()))
                .forEach(player -> player.damage(around.gameConfiguration().borderDamage()));
    }

    public void begin() {
        border.setSize(0, around.gameConfiguration().timeLimit()); // this probably won't work
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            damagePlayersInBorder();
            if (running) {
                schedule();
            }
        }, around.gameConfiguration().borderDamageTime());
    }
}
