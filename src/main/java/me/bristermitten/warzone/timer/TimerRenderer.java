package me.bristermitten.warzone.timer;

import me.bristermitten.warzone.chat.ChatFormatter;
import me.bristermitten.warzone.lang.LangConfig;
import me.bristermitten.warzone.util.DurationFormatter;

import javax.inject.Inject;
import javax.inject.Provider;

public class TimerRenderer {
    private final Provider<LangConfig> langConfigProvider;
    private final ChatFormatter chatFormatter;

    @Inject
    public TimerRenderer(Provider<LangConfig> langConfigProvider, ChatFormatter chatFormatter) {
        this.langConfigProvider = langConfigProvider;
        this.chatFormatter = chatFormatter;
    }

    public String render(Timer timer) {
        return render(timer, langConfigProvider.get().gameLang().noTimeRemaining());
    }

    public String render(Timer timer, String defaultMessage) {
        if (!timer.hasStarted()) {
            return chatFormatter.preFormat(defaultMessage, null);
        }
        var timeRemaining = timer.getTimeRemaining();
        if (timeRemaining <= 0) {
            return chatFormatter.preFormat(defaultMessage, null);
        }
        return DurationFormatter.format(timeRemaining);
    }
}
