package me.bristermitten.warzone.loot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LootGenerator {

    public @Unmodifiable List<ItemStack> generateLoot(LootTable table) {
        var random = ThreadLocalRandom.current();
        return table.getItems()
                .filter(entry -> random.nextDouble() < entry.chance())
                .map(entry -> entry.item().getItem().asQuantity(entry.min() == entry.max() ? entry.max() : random.nextInt(entry.min(), entry.max() + 1)))
                .toJavaList();
    }
}
