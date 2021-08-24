package me.bristermitten.warzone.game.world;

import com.google.common.base.Functions;
import io.vavr.collection.List;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.util.Time;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameBorder {
    private final WorldBorder border;
    private final World world;
    private final Arena around;

    private final Map<UUID, Long> playerDamageTimes = new HashMap<>();

    public GameBorder(Arena around) {
        this.world = around.forceGetWorld();
        this.border = world.getWorldBorder();
        this.around = around;
    }

    public void damagePlayersInBorder() {
        List.ofAll(Bukkit.getOnlinePlayers())
                .filter(player -> player.getWorld().equals(world))
                .filter(player -> !border.isInside(player.getLocation()))
                .filter(player -> Time.millisToTicks(System.currentTimeMillis() - playerDamageTimes.computeIfAbsent(player.getUniqueId(), Functions.constant(0L))) >= around.gameConfig().borderDamageTime())
                .peek(player -> playerDamageTimes.put(player.getUniqueId(), System.currentTimeMillis()))
                .forEach(player -> player.damage(around.gameConfig().borderDamage()));
    }

    public void begin() {
        border.setCenter(around.playableArea().center().toLocation(world));
        border.setSize(around.playableArea().longestSizeLength());
        border.setSize(0, around.gameConfig().timeLimit()); // this probably won't work
        border.setDamageAmount(0);
        border.setDamageBuffer(0);
    }


    public void pause() {
        border.setSize(border.getSize());
    }
}
