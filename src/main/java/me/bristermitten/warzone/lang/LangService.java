package me.bristermitten.warzone.lang;

import io.vavr.collection.HashMap;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;
import java.util.function.Function;

public class LangService {
    private final ChatFormatter formatter;
    private final Provider<LangConfig> configProvider;

    @Inject
    public LangService(ChatFormatter formatter, Provider<LangConfig> configProvider) {
        this.formatter = formatter;
        this.configProvider = configProvider;
    }

    public void sendMessage(@NotNull Player receiver, String message, @NotNull Map<String, Object> placeholders) {
        var replaced = HashMap.ofAll(placeholders)
                .foldLeft(message, (s, pattern) -> s.replace(pattern._1(), pattern._2.toString()));

        receiver.sendMessage(formatter.format(replaced, receiver));
    }

    public void sendMessage(@NotNull Player receiver, @NotNull Function<LangConfig, String> message) {
        sendMessage(receiver, message, Map.of());
    }

    public void sendTitle(@NotNull Player receiver, @NotNull String title, @NotNull String subtitle) {
        var formattedTitle = formatter.format(title, receiver);
        var formattedSubtitle = formatter.format(subtitle, receiver);
        receiver.showTitle(Title.title(formattedTitle, formattedSubtitle));
    }

    public void sendTitle(@NotNull Player receiver, @NotNull Function<LangConfig, LangConfig.TitleConfig> titleFunction) {
        var title = titleFunction.apply(configProvider.get());
        sendTitle(receiver, title.title(), title.subtitle());
    }

    public void sendMessage(@NotNull Player receiver, @NotNull Function<LangConfig, String> message, @NotNull Map<String, Object> placeholders) {
        sendMessage(receiver, message.apply(configProvider.get()), placeholders);
    }
}
