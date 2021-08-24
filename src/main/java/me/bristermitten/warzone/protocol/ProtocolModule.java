package me.bristermitten.warzone.protocol;

import com.comphenix.protocol.ProtocolManager;
import com.google.inject.AbstractModule;

public class ProtocolModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProtocolManager.class).toProvider(ProtocolManagerProvider.class);
    }
}
