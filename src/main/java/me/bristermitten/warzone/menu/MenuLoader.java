package me.bristermitten.warzone.menu;

import com.google.common.primitives.Ints;
import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.util.Null;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Singleton
public class MenuLoader {
    public static final String GLOBAL_NAME = "global";
    private final ChatFormatter baseFormatter;
    private final Plugin plugin;

    @Inject
    public MenuLoader(ChatFormatter formatter, Plugin plugin) {
        this.baseFormatter = formatter;
        this.plugin = plugin;
    }

    public ChatFormatter generateFormatter(OfflinePlayer opener, String pageTitle) {
        return baseFormatter.withHooks(
                (message, player) -> message
                        .replace("{gui_opener_name}", Null.get(opener.getName(), "Can't retrieve name"))
                        .replace("{gui_opener_uuid}", opener.getUniqueId().toString())
                        .replace("{page_number}", pageTitle));
    }

    public @NotNull MenuItem loadItem(OfflinePlayer player, MenuConfig.PageConfig.ItemConfig itemConfig, ChatFormatter formatter) {
        var type = itemConfig.type();
        var amount = Null.get(itemConfig.amount(), 1);
        var name = itemConfig.name();
        var lore = Null.get(itemConfig.lore(), new ArrayList<String>());
        var slots = Null.get(itemConfig.slots(), new ArrayList<Integer>());
        var action = Null.get(itemConfig.action(), MenuAction.NOTHING);

        var item = new ItemStack(type, amount);
        var meta = item.getItemMeta();
        meta.displayName(formatter.format(name, player));
        meta.lore(lore.stream().map(s -> formatter.format(s, player)).toList());

        if (itemConfig.headOwner() != null && meta instanceof SkullMeta headMeta) {
            var owner = formatter.preFormat(itemConfig.headOwner(), player);
            headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
            meta = headMeta;
        }
        item.setItemMeta(meta);

        return new MenuItem(item, slots, action);
    }

    private Page loadPage(OfflinePlayer player, MenuConfig.PageConfig globalPage, String name, MenuConfig.PageConfig config) {
        var formatter = generateFormatter(player, name);

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
                .values().stream()
                .map(itemConfig -> loadItem(player, itemConfig, formatter))
                .toList();

        return new Page(name, size, title, items);
    }

    public Menu load(OfflinePlayer player, MenuConfig config) {
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


        var menu = new Menu(pages.get(0), pages);
        pages.forEach(page -> page.bind(menu, plugin));
        return menu;
    }
}
