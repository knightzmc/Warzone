package me.bristermitten.warzone.game.border;

import com.google.common.base.Functions;
import io.vavr.collection.List;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.task.Task;
import me.bristermitten.warzone.util.Time;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameBorder extends Task {
    private final WorldBorder border;
    private final World world;
    private final Arena around;
    private final Plugin plugin;
    private final Map<UUID, Long> playerDamageTimes = new HashMap<>();

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
                .filter(player -> Time.millisToTicks(System.currentTimeMillis() - playerDamageTimes.computeIfAbsent(player.getUniqueId(), Functions.constant(0L))) >= around.gameConfiguration().borderDamageTime())
                .peek(player -> playerDamageTimes.put(player.getUniqueId(), System.currentTimeMillis()))
                .forEach(player -> player.damage(around.gameConfiguration().borderDamage()));
    }

    public void begin() {
        border.setCenter(around.playableArea().center().toLocation(world));
        border.setSize(around.playableArea().longestSizeLength());
        border.setSize(0, around.gameConfiguration().timeLimit()); // this probably won't work
        border.setDamageAmount(0);
        border.setDamageBuffer(0);
        start();
    }

    @Override
    protected void schedule() {
        runningTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            damagePlayersInBorder();
            if (running) {
                schedule();
            }
        }, 5);
    }
}
