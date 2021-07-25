package me.bristermitten.warzone.chat;

import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class MiniMessageFactory {
    private final MiniMessagePlaceholders placeholders;

    @Inject
    public MiniMessageFactory(MiniMessagePlaceholders placeholders) {
        this.placeholders = placeholders;
    }

    public MiniMessage create(@Nullable Player player) {
        var placeholders = this.placeholders.get();
        return MiniMessage.builder()
                .placeholderResolver(placeholder -> placeholders.get(placeholder)
                        .map(f -> f.apply(player))
                        .getOrElse((ComponentLike) null))
                .build();
    }
}
