package me.bristermitten.warzone.chat.channel;

import me.bristermitten.warzone.chat.ChatConfig;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Channels {

    private final ChatChannel inGame;
    private final ChatChannel preGameLobby;
    private final ChatChannel lobby;

    @Inject
    public Channels(Provider<ChatConfig> config) {
        inGame = new ChatChannel("In Game", () -> config.get().formats().inGame());
        preGameLobby = new ChatChannel("Pre-game Lobby", () -> config.get().formats().preGameLobby());
        lobby = new ChatChannel(" Lobby", () -> config.get().formats().lobby());
    }

    public ChatChannel getInGame() {
        return inGame;
    }

    public ChatChannel getPreGameLobby() {
        return preGameLobby;
    }

    public ChatChannel getLobby() {
        return lobby;
    }

}
