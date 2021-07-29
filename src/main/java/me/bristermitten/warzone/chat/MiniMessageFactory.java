package me.bristermitten.warzone.chat;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public record MiniMessageFactory(MiniMessagePlaceholders miniMessagePlaceholders) {
    @Inject
    public MiniMessageFactory {
    }

    public MiniMessage create(@Nullable Player player) {
        var placeholders = miniMessagePlaceholders.get();
        return MiniMessage.builder()
                .placeholderResolver(placeholder -> placeholders.get(placeholder)
                        .map(f -> f.apply(player))
                        .getOrElse((ComponentLike) null))
                .build();
    }
}
