package me.bristermitten.warzone.chat;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import me.bristermitten.warzone.chat.channel.ChannelModule;
import me.bristermitten.warzone.chat.hook.ChatHook;
import me.bristermitten.warzone.chat.hook.PAPIChatHook;

public class ChatModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ChannelModule());

        bind(ChatManager.class).to(SimpleChatManager.class);
        bind(ChatFormatter.class).to(MiniMessageFormatter.class);
        
        Multibinder.newSetBinder(
                binder(), ChatHook.class
        ).addBinding().to(PAPIChatHook.class);
    }
}
