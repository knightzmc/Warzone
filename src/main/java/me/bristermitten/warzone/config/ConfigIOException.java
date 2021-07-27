package me.bristermitten.warzone.config;

public class ConfigIOException extends RuntimeException {
    public ConfigIOException(Throwable cause) {
        super(cause);
    }

    public ConfigIOException(String message) {
        super(message);
    }

    public ConfigIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
