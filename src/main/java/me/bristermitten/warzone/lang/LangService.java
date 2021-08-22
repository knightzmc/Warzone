package me.bristermitten.warzone.lang;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class LangService {
    private final ChatFormatter formatter;
    private final Provider<LangConfig> configProvider;

    @Inject
    public LangService(ChatFormatter formatter, Provider<LangConfig> configProvider) {
        this.formatter = formatter;
        this.configProvider = configProvider;
    }

    public void send(@NotNull Player receiver, LangElement message, @NotNull Map<String, Object> placeholders) {
        UnaryOperator<String> applyPlaceholders = str -> HashMap.ofAll(placeholders)
                .foldLeft(str, (s, pattern) -> s.replace(pattern._1(), pattern._2.toString()));

        if (message.message() != null) {
            var replaced = applyPlaceholders.apply(message.message());
            sendMessage(receiver, replaced);
        }

        if (message.title() != null || message.subtitle() != null) {
            var replacedTitle = Option.of(message.title()).map(applyPlaceholders)
                    .getOrElse("");
            var replacedSubtitle = Option.of(message.subtitle()).map(applyPlaceholders)
                    .getOrElse("");
            sendTitle(receiver, replacedTitle, replacedSubtitle);
        }
    }

    public void sendMessage(@NotNull Player receiver, @NotNull Function<LangConfig, LangElement> message) {
        sendMessage(receiver, message, Map.of());
    }

    public void sendMessage(@NotNull Player receiver, @NotNull Function<LangConfig, LangElement> message, @NotNull Map<String, Object> placeholders) {
        send(receiver, message.apply(configProvider.get()), placeholders);
    }

    public void sendMessage(@NotNull Player receiver, @NotNull String message) {
        receiver.sendMessage(formatter.format(message, receiver));
    }

    public void sendTitle(@NotNull Player receiver, @NotNull String title, @NotNull String subtitle) {
        var formattedTitle = formatter.format(title, receiver);
        var formattedSubtitle = formatter.format(subtitle, receiver);
        receiver.showTitle(Title.title(formattedTitle, formattedSubtitle));
    }
}
