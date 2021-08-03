package me.bristermitten.warzone.menu;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.logging.Logger;

public record Menu(Page globalPage, List<Page> pages) {
    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());

    public @Unmodifiable List<Page> getPages() {
        return List.copyOf(pages);
    }


    public void addFirstEmpty(MenuItem item) {
        for (Page page : pages) {
            if (page.addFirstEmpty(item)) {
                return;
            }
        }
    }

    public void add(Plugin plugin, MenuItem item) {
        for (Page page : pages) {
            if (page.add(item)) {
                return;
            }
        }
        Page newPage = addPage(plugin);
        if (!newPage.add(item)) {
            LOGGER.warning(() -> "Despite adding a new page there was still no room left in the Menu " + this);
        }
    }

    public void open(Player player) {
        pages.get(0).open(player);
    }

    public Page addPage(@NotNull Plugin plugin) {
        globalPage.bind(this, plugin);
        Page newPage = globalPage.copy();
        newPage.bind(this, plugin);
        pages.add(newPage);
        return newPage;
    }

    public Page firstEmptyPage(Plugin plugin) {
        for (Page page : pages) {
            if (page.firstEmpty() != -1) {
                return page;
            }
        }
        return addPage(plugin);
    }
}
