package me.bristermitten.warzone.menu;

import me.bristermitten.warzone.util.Null;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class MenuConfigLoader {
    private final Logger logger = Logger.getLogger(MenuConfigLoader.class.getName());

    public @NotNull MenuTemplate load(@NotNull MenuConfig config) {
        return load(config, null);
    }

    public @NotNull MenuTemplate load(@NotNull MenuConfig config, @Nullable MenuConfig defaultConfig) {
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
        var title = Null.get(config.title(), Optional.ofNullable(defaultConfig).map(MenuConfig::title).orElse("No Title Set"));
        Integer defaultSize = Optional.ofNullable(defaultConfig).map(MenuConfig::size).orElse(54);
        int size = Null.get(config.size(), defaultSize);
        if (size == 0) {
            size = defaultSize;
        }
        return new MenuTemplate(title, size, Map.copyOf(map));
    }

}
