package me.bristermitten.warzone.menu;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.chat.hook.ChatHook;
import me.bristermitten.warzone.item.ItemRenderer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import javax.inject.Inject;

public class MenuRenderer {

    private final ChatFormatter baseFormatter;
    private final ItemRenderer renderer;

    @Inject
    public MenuRenderer(ChatFormatter baseFormatter, ItemRenderer renderer) {
        this.baseFormatter = baseFormatter;
        this.renderer = renderer;
    }

    public Inventory render(MenuTemplate template, Player viewer, ChatHook... hooks) {
        var formatter = baseFormatter.withHooks(hooks);
        var title = formatter.format(template.title(), viewer);
        var inv = Bukkit.createInventory(null, template.size(), title);
        template.items().forEach((slot, item) -> {
            var rendered = renderer.render(item.item(), formatter, viewer);
            inv.setItem(slot, rendered);
        });
        return inv;
    }


}
