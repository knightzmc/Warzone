package me.bristermitten.warzone.chat.channel;

import me.bristermitten.warzone.chat.ChatConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class Channels {

    private final @NotNull ChatChannel inGame;
    private final @NotNull ChatChannel preGameLobby;
    private final @NotNull ChatChannel lobby;

    @Inject
    public Channels(@NotNull Provider<ChatConfig> config) {
        inGame = new ChatChannel("In Game", () -> config.get().formats().inGame());
        preGameLobby = new ChatChannel("Pre-game Lobby", () -> config.get().formats().preGameLobby());
        lobby = new ChatChannel(" Lobby", () -> config.get().formats().lobby());
    }

    public @NotNull ChatChannel getInGame() {
        return inGame;
    }

    public @NotNull ChatChannel getPreGameLobby() {
        return preGameLobby;
    }

    public @NotNull ChatChannel getLobby() {
        return lobby;
    }

}
