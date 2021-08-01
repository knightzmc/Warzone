package me.bristermitten.warzone.menu;

import com.google.common.primitives.Ints;
import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.util.Null;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public class MenuLoader {
    public static final String GLOBAL_NAME = "global";
    private final ChatFormatter formatter;
    private final Plugin plugin;

    @Inject
    public MenuLoader(ChatFormatter formatter, Plugin plugin) {
        this.formatter = formatter;
        this.plugin = plugin;
    }

    public @NotNull MenuItem loadItem(Player player, String itemName, MenuConfig.PageConfig.ItemConfig itemConfig) {
        var type = itemConfig.type();
        var amount = Null.get(itemConfig.amount(), 1);
        var name = itemConfig.name();
        var lore = Null.get(itemConfig.lore(), new ArrayList<String>());
        var slots = itemConfig.slots();
        var action = Null.get(itemConfig.action(), MenuAction.NOTHING);

        var item = new ItemStack(type, amount);
        var meta = item.getItemMeta();
        meta.displayName(formatter.format(name, player));
        meta.lore(lore.stream().map(s -> formatter.format(s, player)).toList());

        if (itemConfig.headOwner() != null && meta instanceof SkullMeta headMeta) {
            //noinspection deprecation cringe
            headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(itemConfig.headOwner()));
            meta = headMeta;
        }
        item.setItemMeta(meta);

        return new MenuItem(itemName, item, slots, action);
    }

    private Page loadPage(Player player, MenuConfig.PageConfig globalPage, String name, MenuConfig.PageConfig config) {
        var titleString = Null.get(config.title(), Objects.requireNonNull(globalPage.title()));
        var title = formatter.format(titleString, player);
        var size = Null.get(config.size(), Objects.requireNonNull(globalPage.size()));

        final var configItems = new HashMap<>(Null.get(config.items(), Objects.requireNonNull(globalPage.items())));
        globalPage.items().forEach((k, v) -> {
            if (!configItems.containsKey(k)) {
                configItems.put(k, v);
            }
        });

        var items = configItems
                .entrySet().stream()
                .map(e -> loadItem(player, e.getKey(), e.getValue()))
                .toList();

        var inventory = Bukkit.createInventory(null, size, title);
        items.forEach(item -> item.slots()
                .forEach(slot -> inventory.setItem(slot, item.item())));

        return new Page(name, inventory, items);
    }

    public Menu load(Player player, MenuConfig config) {
        var globalPageConfig =
                Null.get(config.pages().get(GLOBAL_NAME), MenuConfig.PageConfig.DEFAULT);

        final List<Page> pages = config.pages().entrySet().stream()
                .sorted((o1, o2) -> {
                    if (o1.getKey().equals(GLOBAL_NAME)) {
                        return Integer.MAX_VALUE;
                    }
                    if (o2.getKey().equals(GLOBAL_NAME)) {
                        return Integer.MIN_VALUE;
                    }
                    var i1 = Ints.tryParse(o1.getKey());
                    var i2 = Ints.tryParse(o2.getKey());
                    if (i1 != null && i2 != null) {
                        return i1.compareTo(i2);
                    }
                    return 0;
                })

                .map(e -> loadPage(player, globalPageConfig, e.getKey(), e.getValue()))
                .toList();


        var menu = new Menu(pages);
        pages.forEach(page -> page.bind(menu, plugin));
        return menu;
    }
}
