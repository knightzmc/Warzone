package me.bristermitten.warzone.chat;

import io.vavr.collection.List;
import me.bristermitten.warzone.chat.hook.ChatHook;
import net.kyori.adventure.text.Component;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public record MiniMessageFormatter(MiniMessageFactory miniMessageFactory,
                                   Set<ChatHook> chatHooks) implements ChatFormatter {
    @Inject
    public MiniMessageFormatter {
        // shut the frick up sonar lint
    }

    @Override
    public String preFormat(String message, @Nullable OfflinePlayer player) {
        return List.ofAll(chatHooks)
                .foldLeft(message, (m, r) -> r.format(m, player));
    }

    @Override
    public ChatFormatter withHooks(ChatHook... hooks) {
        var copy = new HashSet<>(chatHooks);
        copy.addAll(Arrays.asList(hooks));
        return new MiniMessageFormatter(miniMessageFactory, copy);
    }

    @Override

    public Component format(String message, @Nullable OfflinePlayer player) {
        var formatted = preFormat(message, player);
        return miniMessageFactory.create(player)
                .parse(formatted);
    }
}
