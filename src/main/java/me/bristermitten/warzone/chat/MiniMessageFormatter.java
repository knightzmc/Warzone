package me.bristermitten.warzone.chat;

import me.bristermitten.warzone.chat.hook.ChatHook;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Set;

public record MiniMessageFormatter(MiniMessageFactory miniMessageFactory,
                                   Set<ChatHook> chatHooks) implements ChatFormatter {
    @Inject
    public MiniMessageFormatter {
    }

    @Override
    public String preFormat(String message, @Nullable Player player) {
        return io.vavr.collection.List.ofAll(chatHooks)
                .foldLeft(message, (m, r) -> r.format(m, player));
    }

    @Override

    public Component format(String message, @Nullable Player player) {
        var formatted = preFormat(message, player);
        return miniMessageFactory.create(player)
                .parse(formatted);
    }
}
