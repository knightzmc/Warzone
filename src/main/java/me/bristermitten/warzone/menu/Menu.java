package me.bristermitten.warzone.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.logging.Logger;

public record Menu(Page globalPage, List<Page> pages) {
    private static final Logger LOGGER = Logger.getLogger(Menu.class.getName());

    public @Unmodifiable List<Page> getPages() {
        return List.copyOf(pages);
    }


    public void add(MenuItem item) {
        for (Page page : pages) {
            if (page.add(item)) {
                return;
            }
        }
        Page newPage = globalPage.copy();
        pages.add(newPage);
        if (!newPage.add(item)) {
            LOGGER.warning(() -> "Despite adding a new page there was still no room left in the Menu " + this);
        }
    }

    public void open(Player player) {
        pages.get(0).open(player);
    }
}
