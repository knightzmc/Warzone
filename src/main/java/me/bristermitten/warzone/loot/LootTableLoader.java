package me.bristermitten.warzone.loot;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.item.ItemRenderer;
import me.bristermitten.warzone.util.Numbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.function.Function;

public class LootTableLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(LootTableLoader.class);
    private final ItemRenderer renderer;
    private final ChatFormatter formatter;

    @Inject
    public LootTableLoader(ItemRenderer renderer, ChatFormatter formatter) {
        this.renderer = renderer;
        this.formatter = formatter;
    }

    public Map<String, LootTable> load(LootTablesConfig config) {

        var customItems = HashMap.ofAll(config.items())
                .mapValues(item -> renderer.render(item, formatter, null));

        return HashMap.ofAll(config.tables())
                .map((name, entries) -> {
                    var tableEntries = List.ofAll(entries).map(entry -> {
                        var item = customItems.get(entry.item())
                                .map(NormalTableItem::new)
                                .map(TableItem.class::cast)
                                .getOrElse(() -> new QATableItem(entry.item()));

                        var chance = entry.chance();
                        if (chance > 1.0) {
                            LOGGER.warn("Item chances should not be more than 1.0 but {} was!", name);
                            chance = 1f;
                        }

                        var range = Numbers.minMax(entry.min(), entry.max());

                        return new TableEntry(
                                item,
                                chance,
                                range._1,
                                range._2
                        );
                    });
                    return Tuple.of(name, tableEntries);
                })
                .map(t -> new LootTable(t._1, t._2))
                .toMap(LootTable::getName, Function.identity());
    }
}
