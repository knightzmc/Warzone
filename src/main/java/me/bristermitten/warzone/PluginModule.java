package me.bristermitten.warzone;

import com.google.inject.AbstractModule;
import org.bukkit.plugin.Plugin;

public class PluginModule extends AbstractModule {
    private final Plugin plugin;

    public PluginModule(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(plugin);
    }
}
