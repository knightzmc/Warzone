package me.bristermitten.warzone.chat;

import io.vavr.Function2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.control.Option;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.function.Function;

import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.toComponent;

public class MiniMessagePlaceholders implements Provider<Map<String, Function<@Nullable OfflinePlayer, ComponentLike>>> {
    private final Provider<ChatConfig> configProvider;
    private final ChatFormatter chatFormatter;

    @Inject
    public MiniMessagePlaceholders(Provider<ChatConfig> configProvider, ChatFormatter chatFormatter) {
        this.configProvider = configProvider;
        this.chatFormatter = chatFormatter;
    }

    @Override
    public @NotNull Map<String, Function<@Nullable OfflinePlayer, ComponentLike>> get() {
        var config = configProvider.get();
        return HashMap.of(
                "player_name_hover_stats", player -> {
                    var playerName = Option.of(player)
                            .map(OfflinePlayer::getName).getOrElseThrow(() -> new IllegalStateException("Player who is offline sent a message!"));
                    return Component.text(playerName)
                            .hoverEvent(config.statsHoverMessage()
                                    .stream()
                                    .map(Function2.of(chatFormatter::format).reversed().apply(player))
                                    .collect(toComponent(newline())).asHoverEvent())
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/party invite " + playerName));
                }
        );
    }
}
