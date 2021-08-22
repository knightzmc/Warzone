package me.bristermitten.warzone.chat.channel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import me.bristermitten.warzone.listener.ListenerBinding;
import org.jetbrains.annotations.NotNull;

import javax.inject.Named;

public class ChannelModule extends AbstractModule {
    @Override
    protected void configure() {
        ListenerBinding.bindListener(binder()).to(ChatChannelListener.class);
    }

    @Provides
    @Named("inGame")
    @NotNull
    ChatChannel provideInGame(@NotNull Channels channels) {
        return channels.getInGame();
    }

    @Provides
    @Named("lobby")
    @NotNull
    ChatChannel provideInLobby(@NotNull Channels channels) {
        return channels.getLobby();
    }

    @Provides
    @Named("preGameLobby")
    @NotNull
    ChatChannel providePreGameLobby(@NotNull Channels channels) {
        return channels.getPreGameLobby();
    }
}
