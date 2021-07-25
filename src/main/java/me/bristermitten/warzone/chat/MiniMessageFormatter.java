package me.bristermitten.warzone.chat;

import me.bristermitten.warzone.chat.hook.ChatHook;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.List;

public class MiniMessageFormatter implements ChatManager {
    private final MiniMessageFactory miniMessageFactory;

    private final List<ChatHook> chatHooks;

    @Inject
    public MiniMessageFormatter(MiniMessageFactory miniMessageFactory, List<ChatHook> chatHooks) {
        this.miniMessageFactory = miniMessageFactory;
        this.chatHooks = chatHooks;
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
