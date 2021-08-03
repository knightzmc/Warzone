package me.bristermitten.warzone.chat;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public record MiniMessageFactory(MiniMessagePlaceholders miniMessagePlaceholders) {
    @Inject
    public MiniMessageFactory {
        // SHUT UP
    }

    public @NotNull MiniMessage create(@Nullable OfflinePlayer player) {
        var placeholders = miniMessagePlaceholders.get();
        return MiniMessage.builder()
                .placeholderResolver(placeholder -> placeholders.get(placeholder)
                        .map(f -> f.apply(player))
                        .getOrElse((ComponentLike) null))
                .build();
    }
}
