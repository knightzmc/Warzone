package me.bristermitten.warzone.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import me.bristermitten.warzone.config.gson.GsonProvider;
import me.bristermitten.warzone.config.loading.*;
import me.bristermitten.warzone.config.mapper.GsonObjectMapper;
import me.bristermitten.warzone.config.mapper.ObjectMapper;
import me.bristermitten.warzone.config.saving.ObjectSaver;
import me.bristermitten.warzone.config.saving.YamlObjectSaver;
import me.bristermitten.warzone.config.yaml.SnakeYamlProvider;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Provider;
import java.util.Map;

public class ConfigModule extends AbstractModule {
    private final Map<Class<?>, ConfigurationProvider<?>> configs;

    public ConfigModule(Map<Class<?>, ConfigurationProvider<?>> configs) {
        this.configs = configs;
    }

    @Override
    protected void configure() {
        bind(Yaml.class).toProvider(SnakeYamlProvider.class);
        bind(Gson.class).toProvider(GsonProvider.class);

        bind(ObjectMapper.class).to(GsonObjectMapper.class);
        bind(ObjectLoader.class).to(YamlObjectLoader.class);
        bind(ObjectSaver.class).to(YamlObjectSaver.class);

        configs.forEach((key, provider) -> {
            //noinspection unchecked god i hate this
            var clazz = (Class<? super Object>) key;
            //noinspection unchecked
            var configProvider = (Provider<? super Object>) provider;
            bind(clazz).toProvider(configProvider);
            //noinspection unchecked pure evil
            bind((TypeLiteral<ConfigurationProvider<?>>) TypeLiteral.get(TypeToken.getParameterized(ConfigurationProvider.class, clazz).getType()))
                    .toInstance((ConfigurationProvider<? super Object>) configProvider);

        });

    }
}
