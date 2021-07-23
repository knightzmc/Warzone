package me.bristermitten.warzone;

import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.bukkit.plugin.Plugin;

public class PluginAspect implements Aspect {
    private final Plugin plugin;

    public PluginAspect(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Module generateModule() throws IllegalStateException {
        return new PluginModule(plugin);
    }


}
