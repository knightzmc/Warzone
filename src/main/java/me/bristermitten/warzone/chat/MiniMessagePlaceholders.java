package me.bristermitten.warzone.chat;

import io.vavr.Function2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.toComponent;

public class MiniMessagePlaceholders implements Provider<Map<String, Function<@Nullable Player, ComponentLike>>> {
    private final Provider<ChatConfig> configProvider;
    private final ChatFormatter chatFormatter;

    @Inject
    public MiniMessagePlaceholders(Provider<ChatConfig> configProvider, ChatFormatter chatFormatter) {
        this.configProvider = configProvider;
        this.chatFormatter = chatFormatter;
    }

    @Override
    public Map<String, Function<@Nullable Player, ComponentLike>> get() {
        var config = configProvider.get();
        return HashMap.of(
                "player_name_hover_stats", player ->
                        Component.text(Option.of(player).map(Player::getName).getOrElse("[Server]")) // throw an exception?
                                .hoverEvent(config.statsHoverMessage()
                                        .stream()
                                        .map(Function2.of(chatFormatter::format).reversed().apply(player))
                                        .collect(toComponent(newline())).asHoverEvent())
        );
    }
}
