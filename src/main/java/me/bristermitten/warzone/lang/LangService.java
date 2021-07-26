package me.bristermitten.warzone.lang;

import io.vavr.collection.HashMap;
import me.bristermitten.warzone.chat.ChatFormatter;
import org.bukkit.entity.Player;

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

    public void sendMessage(Player receiver, String message, Map<String, Object> placeholders) {
        var replaced = HashMap.ofAll(placeholders)
                .foldLeft(message, (s, pattern) -> s.replace(pattern._1(), pattern._2.toString()));

        receiver.sendMessage(formatter.format(replaced, receiver));
    }

    public void sendMessage(Player receiver, Function<LangConfig, String> message, Map<String, Object> placeholders) {
        sendMessage(receiver, message.apply(configProvider.get()), placeholders);
    }
}
