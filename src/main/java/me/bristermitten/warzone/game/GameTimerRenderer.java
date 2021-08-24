package me.bristermitten.warzone.game;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.util.DurationFormatter;

import javax.inject.Inject;
import javax.inject.Provider;

public class GameTimerRenderer {
    private final Provider<LangConfig> langConfigProvider;
    private final ChatFormatter chatFormatter;

    @Inject
    public GameTimerRenderer(Provider<LangConfig> langConfigProvider, ChatFormatter chatFormatter) {
        this.langConfigProvider = langConfigProvider;
        this.chatFormatter = chatFormatter;
    }

    public String render(GameTimer gameTimer) {
        var timeRemaining = gameTimer.getTimeRemaining();
        if (timeRemaining <= 0) {
            return chatFormatter.preFormat(langConfigProvider.get().gameLang().noTimeRemaining().message(), null);
        }
        return DurationFormatter.format(timeRemaining);
    }
}
