package me.bristermitten.warzone.database;

import com.zaxxer.hikari.HikariConfig;

import javax.inject.Inject;
import javax.inject.Provider;

public record HikariConfigurationProvider(
        Provider<DatabaseConfig> configurationProvider) implements Provider<HikariConfig> {
    @Inject
    public HikariConfigurationProvider {
    }


    @Override
    public HikariConfig get() {
        DatabaseConfig config = configurationProvider.get();
        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.type().createJDBCUrl(config));
        hikariConfig.setUsername(config.username());
        hikariConfig.setPassword(config.password());
        return hikariConfig;
    }
}
