package me.bristermitten.warzone.tags;

import me.bristermitten.warzone.hooks.TagFormatter;
import me.bristermitten.warzone.lang.LangService;
import me.bristermitten.warzone.listener.EventListener;
import me.bristermitten.warzone.player.xp.LevelUpEvent;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.HashSet;
import java.util.Map;
import java.util.SplittableRandom;

public class TagsListener implements EventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagsListener.class);
    private final Provider<TagsConfig> tagsConfigProvider;
    private final SplittableRandom random = new SplittableRandom();
    private final Permission permission;
    private final LangService langService;
    private final TagFormatter tagFormatter;

    @Inject
    TagsListener(Provider<TagsConfig> tagsConfigProvider, Permission permission, LangService langService, TagFormatter tagFormatter) {
        this.tagsConfigProvider = tagsConfigProvider;
        this.permission = permission;
        this.langService = langService;
        this.tagFormatter = tagFormatter;
    }

    @EventHandler
    public void onLevelUp(LevelUpEvent event) {
        var tagsConfig = tagsConfigProvider.get();
        if (event.getNewLevel() % tagsConfig.tagRate() != 0) {
            return;
        }
        if (random.nextDouble(0, 100) > tagsConfig.chance()) {
            return;
        }
        var checked = new HashSet<String>();
        String tag;
        do {
            tag = tagsConfig.tags().get(random.nextInt(tagsConfig.tags().size()));
            checked.add(tag);
            if (checked.size() == tagsConfig.tags().size()) {
                event.getLevelling().getPlayer().peek(player ->
                        langService.send(player, langConfig -> langConfig.tagsLang().noMoreTags()));
                LOGGER.warn("Player {} already has every tag", event.getLevelling().getPlayerId());
                return;
            }
        } while (permission.playerHas(null, event.getLevelling().getOfflinePlayer(), tag));

        permission.playerAdd(null, event.getLevelling().getOfflinePlayer(), tag);
        String finalTag = tag;
        event.getLevelling().getPlayer().peek(player ->
                langService.send(player, langConfig -> langConfig.tagsLang().tagGained(),
                        Map.of("{tag}", tagFormatter.format(finalTag))));
    }
}
