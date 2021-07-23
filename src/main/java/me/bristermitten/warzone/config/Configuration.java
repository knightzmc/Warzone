package me.bristermitten.warzone.config;

/*
Take file from jar
If doesn't exist, copy
load into a ConfigurationProvider<T>
 */
public record Configuration<T>(Class<T> type, String path) {
}
