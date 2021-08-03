package me.bristermitten.warzone.menu;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.util.Null;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class ItemRenderer {


    public ItemStack render(MenuConfig.ItemConfig config, ChatFormatter formatter, OfflinePlayer viewer) {
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
                skullMeta.setOwningPlayer(
                        Bukkit.getOfflinePlayer(UUID.fromString(formatter.preFormat(config.headOwner(), viewer)))
                );
            }
        });
        return item;
    }
}
