package me.bristermitten.warzone.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public interface EventListener extends Listener {
    default void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
}
