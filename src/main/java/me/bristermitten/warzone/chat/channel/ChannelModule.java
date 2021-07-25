package me.bristermitten.warzone.chat.channel;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Named;

public class ChannelModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ChatChannelListener.class).asEagerSingleton();
    }

    @Provides
    @Named("inGame")
    ChatChannel provideInGame(Channels channels) {
        return channels.getInGame();
    }

    @Provides
    @Named("lobby")
    ChatChannel provideInLobby(Channels channels) {
        return channels.getLobby();
    }

    @Provides
    @Named("preGameLobby")
    ChatChannel providePreGameLobby(Channels channels) {
        return channels.getPreGameLobby();
    }
}
