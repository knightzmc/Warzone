package me.bristermitten.warzone.game.leavemenu;

import me.bristermitten.warzone.menu.MenuConfig;
import me.bristermitten.warzone.menu.MenuConfigLoader;
import me.bristermitten.warzone.menu.MenuTemplate;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public class LeaveRequeueMenuLoader {
    private final Provider<LeaveRequeueMenuConfig> configProvider;

    private final MenuConfigLoader configLoader;

    @Inject
    public LeaveRequeueMenuLoader(Provider<LeaveRequeueMenuConfig> configProvider, MenuConfigLoader configLoader) {
        this.configProvider = configProvider;
        this.configLoader = configLoader;
    }

    public @NotNull MenuTemplate get() {
        var config = configProvider.get().main();
        return configLoader.load(config);
    }
}
