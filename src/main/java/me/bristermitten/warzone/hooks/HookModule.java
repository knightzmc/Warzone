package me.bristermitten.warzone.hooks;

import com.google.inject.AbstractModule;
import net.milkbowl.vault.permission.Permission;

public class HookModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Permission.class).toProvider(PermissionProvider.class);
    }
}
