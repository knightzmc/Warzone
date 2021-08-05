package me.bristermitten.warzone.menu;

import me.bristermitten.warzone.util.Null;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MenuConfigLoader {
    private final Logger logger = LoggerFactory.getLogger(MenuConfigLoader.class);

    public @NotNull MenuTemplate load(@NotNull MenuConfig config) {
        return load(config, null);
    }

    private void loadInto(Map.Entry<String, MenuConfig.MenuItemConfig> entry, Map<Integer, MenuConfig.MenuItemConfig> map, boolean overwrite) {
        var name = entry.getKey();
        var item = entry.getValue();
        if (item.slots() == null) {
            logger.warn("Item {} has no slots but it was configured to be inside a menu. It will not be rendered.", name);
            return;
        }
        for (int slot : item.slots()) {
            if (map.containsKey(slot)) {
                if (!overwrite) {
                    continue;
                }
                logger.warn("Multiple items share slot {}. Conflicting items = {} and {}", slot, name, map.get(slot));
            }
            map.put(slot, item);
        }
    }


    @NotNull
    public MenuTemplate load(@NotNull MenuConfig config, @Nullable MenuConfig defaultConfig) {
        var itemsMap = new HashMap<Integer, MenuConfig.MenuItemConfig>();
        for (var entry : config.items().entrySet()) {
            loadInto(entry, itemsMap, true);
        }
        if (defaultConfig != null) {
            defaultConfig.items().entrySet().forEach(entry -> loadInto(entry, itemsMap, false));
        }

        var title = Null.get(config.title(), Optional.ofNullable(defaultConfig).map(MenuConfig::title).orElse("No Title Set"));
        int defaultSize = Optional.ofNullable(defaultConfig).map(MenuConfig::size).orElse(54);
        int size = Null.get(config.size(), defaultSize);
        if (size == 0) {
            size = defaultSize;
        }
        return new MenuTemplate(title, size, Map.copyOf(itemsMap));
    }

}
