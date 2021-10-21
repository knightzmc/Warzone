package me.bristermitten.warzone.bossbar;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.UUID;


@Singleton
public class BossBarManagerImpl implements BossBarManager {
    private final Multimap<CustomBossBar, UUID> bars = HashMultimap.create();
    private final Table<CustomBossBar, UUID, BossBar> bossBarCache = HashBasedTable.create();
    private final BossBarRenderer renderer;


    @Inject
    public BossBarManagerImpl(BossBarRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public void show(UUID player, CustomBossBar bossBar) {
        if (bars.containsEntry(bossBar, player)) {
            return;
        }
        bars.put(bossBar, player);
        update(bossBar);
    }

    @Override
    public void hide(UUID uuid, CustomBossBar bossBar) {
        bars.remove(bossBar, uuid);
        var existing = bossBarCache.get(bossBar, uuid);
        if (existing != null) {
            bossBarCache.remove(bossBar, uuid);
            var player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.hideBossBar(existing);
            }
        }
    }

    @Override
    public void updateAll() {
        bars.keys().forEach(this::update);
    }

    @Override
    public void update(CustomBossBar bossBar) {
        if (bossBar.isPaused()) {
            return;
        }
        var viewers = this.bars.get(bossBar);
        viewers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);

        var cachedBars = bossBarCache.row(bossBar);

        for (UUID viewer : viewers) {
            var player = Objects.requireNonNull(Bukkit.getPlayer(viewer));
            if (cachedBars.containsKey(viewer)) {
                renderer.renderInto(cachedBars.get(viewer), player, bossBar);
            } else {
                var newBar = renderer.render(player, bossBar);
                cachedBars.put(viewer, newBar);
                player.showBossBar(newBar);
            }
        }
    }
}
