package me.bristermitten.warzone.menu;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class MenuConfigLoader {
    private final Logger logger = Logger.getLogger(MenuConfigLoader.class.getName());

    public @NotNull MenuTemplate load(@NotNull MenuConfig config) {
        var map = new HashMap<Integer, MenuConfig.ItemConfig>();
        for (var entry : config.items().entrySet()) {
            var name = entry.getKey();
            var item = entry.getValue();
            if (item.slots() == null) {
                logger.warning(() -> "Item " + name + " has no slots but it was configured to be inside a menu. It will not be rendered.");
                continue;
            }
            for (int slot : item.slots()) {
                if (map.containsKey(slot)) {
                    logger.warning(() -> "Multiple items share slot " + slot + ". Conflicting items = " + name + " and " + map.get(slot));
                }
                map.put(slot, item);
            }
        }
        return new MenuTemplate(config.title(), config.size(), Map.copyOf(map));
    }

}
