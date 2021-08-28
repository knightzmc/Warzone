package me.bristermitten.warzone.vault;

import com.google.inject.AbstractModule;
import net.milkbowl.vault.permission.Permission;

public class VaultModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Permission.class).toProvider(PermissionProvider.class);
    }
}
