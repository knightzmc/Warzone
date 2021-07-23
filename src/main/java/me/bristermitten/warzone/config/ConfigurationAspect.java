package me.bristermitten.warzone.config;

import com.google.inject.Injector;
import com.google.inject.Module;
import me.bristermitten.warzone.aspect.Aspect;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigurationAspect implements Aspect {
    private final Map<Class<?>, ConfigurationProvider<?>> binds;

    public ConfigurationAspect(Set<Configuration<?>> configurations) {
        binds = new HashMap<>();
        for (Configuration<?> configuration : configurations) {
            binds.put(configuration.type(), new SimpleConfigurationProvider<>(configuration));
        }

    }


    @Override
    public Module generateModule() {
        return new ConfigModule(binds);
    }

    @Override
    public void finalizeInjections(Injector injector) {
        for (ConfigurationProvider<?> value : binds.values()) {
            injector.injectMembers(value);
        }
    }
}
