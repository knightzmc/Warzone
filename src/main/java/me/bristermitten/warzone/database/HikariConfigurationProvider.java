package me.bristermitten.warzone.database;

import com.zaxxer.hikari.HikariConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;

public record HikariConfigurationProvider(
        Provider<DatabaseConfig> configurationProvider) implements Provider<HikariConfig> {
    @Inject
    public HikariConfigurationProvider {
    }


    @Override
    public @NotNull HikariConfig get() {
        DatabaseConfig config = configurationProvider.get();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.type().createJdbcUrl(config));
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return hikariConfig;
    }
}
