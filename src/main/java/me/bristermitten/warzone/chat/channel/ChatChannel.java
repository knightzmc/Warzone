package me.bristermitten.warzone.chat.channel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public final class ChatChannel {
    private final String name;
    private final Supplier<String> format;

    public ChatChannel(String name, Supplier<String> format) {
        this.name = name;
        this.format = format;
    }


    public String name() {
        return name;
    }

    public String format() {
        return format.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatChannel that)) return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public @NotNull String toString() {
        return "ChatChannel{" +
               "name='" + name + '\'' +
               '}';
    }
}
