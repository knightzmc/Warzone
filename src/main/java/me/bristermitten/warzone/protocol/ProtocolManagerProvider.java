package me.bristermitten.warzone.protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import javax.inject.Provider;

public class ProtocolManagerProvider implements Provider<ProtocolManager> {
    @Override
    public ProtocolManager get() {
        return ProtocolLibrary.getProtocolManager();
    }
}
