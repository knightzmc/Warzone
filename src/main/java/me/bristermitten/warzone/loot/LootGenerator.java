package me.bristermitten.warzone.loot;

import com.google.inject.Singleton;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class LootGenerator {

    public Collection<ItemStack> generateLoot(LootTable table) {
        var random = ThreadLocalRandom.current();
        return table.getItems()
                .filter(entry -> random.nextDouble() < entry.chance())
                .map(entry -> entry.item().getItem().asQuantity(random.nextInt(entry.min(), entry.max())))
                .toJavaList();
    }
}
