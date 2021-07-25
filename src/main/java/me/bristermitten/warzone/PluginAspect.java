package me.bristermitten.warzone;

import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PluginAspect implements Aspect {
    private final Plugin plugin;

    public PluginAspect(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Module generateModule() throws IllegalStateException {
        return new PluginModule(plugin);
    }


}
