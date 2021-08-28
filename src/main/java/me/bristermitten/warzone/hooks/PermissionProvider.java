package me.bristermitten.warzone.hooks;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;
import javax.inject.Provider;

public class PermissionProvider implements Provider<Permission> {
    private final Plugin plugin;

    @Inject
    PermissionProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Permission get() {
        var service = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (service == null) {
            plugin.getSLF4JLogger().error("Vault could not be loaded! Do you have a permissions plugin installed?");
            Bukkit.getPluginManager().disablePlugin(plugin);
            throw new IllegalStateException();
        }
        return service.getProvider();
    }
}
