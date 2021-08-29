package me.bristermitten.warzone.chat;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.warzone.chat.channel.ChannelModule;
import me.bristermitten.warzone.chat.hook.ChatHook;
import me.bristermitten.warzone.chat.hook.HexColorFixerHook;
import me.bristermitten.warzone.chat.hook.PAPIChatHook;

public class ChatModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ChannelModule());

        bind(ChatManager.class).to(SimpleChatManager.class);
        bind(ChatFormatter.class).to(MiniMessageFormatter.class);

        final var multibinder = Multibinder.newSetBinder(binder(), ChatHook.class);
        multibinder.addBinding().to(PAPIChatHook.class);
        multibinder.addBinding().to(HexColorFixerHook.class);
    }
}
