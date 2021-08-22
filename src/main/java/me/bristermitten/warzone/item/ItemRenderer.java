package me.bristermitten.warzone.item;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.util.Null;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.UUID;

public class ItemRenderer {
    private final ChatFormatter defaultFormatter;

    @Inject
    public ItemRenderer(ChatFormatter defaultFormatter) {
        this.defaultFormatter = defaultFormatter;
    }


    public ItemStack render(ItemConfig config, @Nullable OfflinePlayer viewer) {
        return render(config, defaultFormatter, viewer);
    }

    public ItemStack render(ItemConfig config, ChatFormatter formatter, @Nullable OfflinePlayer viewer) {
        var item = new ItemStack(
                config.type(),
                Null.get(config.amount(), 1)
        );

        item.editMeta(meta -> {
            if (config.name() != null) {
                meta.displayName(formatter.format(config.name(), viewer));
            }
            if (config.lore() != null) {
                meta.lore(config.lore().stream().map(s -> formatter.format(s, viewer)).toList());
            }
            if (config.headOwner() != null && meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(formatter.preFormat(config.headOwner(), viewer))));
            }
            if (config.potion() != null && meta instanceof PotionMeta potionMeta) {
                potionMeta.addCustomEffect(new PotionEffect(
                        config.potion().effect(),
                        config.potion().duration(),
                        config.potion().amplifier()
                ), true);
            }
        });
        return item;
    }
}
