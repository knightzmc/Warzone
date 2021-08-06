package me.bristermitten.warzone.game.init;

import io.vavr.control.Option;
import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.loot.LootGenerator;
import me.bristermitten.warzone.loot.LootTable;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class GameChestFiller {
    private final LootGenerator generator;

    public GameChestFiller(LootGenerator generator) {
        this.generator = generator;
    }

    public void fill(Arena arena) {
        SplittableRandom splittableRandom = new SplittableRandom();
        var world = arena.getWorld().getOrElseThrow(() -> new IllegalStateException("World for arena not present"));
        BlockFinder.findBlocks(world, arena.playableArea());
    }

    public void fill(LootTable table, Chest chest) {
        Inventory inventory = chest.getBlockInventory();
        inventory.clear();
        Collection<ItemStack> items = generator.generateLoot(table);
        var random = ThreadLocalRandom.current();
        for (ItemStack item : items) {
            var slot = random.nextInt(0, inventory.getSize());
            inventory.setItem(slot, item);
        }
    }
}
