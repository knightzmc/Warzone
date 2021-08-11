package me.bristermitten.warzone.chat.channel;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.bristermitten.warzone.chat.ChatManager;
import me.bristermitten.warzone.player.PlayerManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class ChatChannelListener implements Listener {
    private final ChatManager chatManager;
    private final PlayerManager playerManager;

    @Inject
    ChatChannelListener(@NotNull Plugin plugin, ChatManager chatManager, PlayerManager playerManager) {
        this.chatManager = chatManager;
        this.playerManager = playerManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChat(@NotNull AsyncChatEvent event) {
        event.setCancelled(true); // cancel default thing
        playerManager.loadPlayer(event.getPlayer().getUniqueId(),
                chatter -> {
                    var channel = chatter.getCurrentState().getChannel();

                    var message = LegacyComponentSerializer.legacySection().serialize(event.originalMessage());
                    chatManager.sendMessage(channel, message, event.getPlayer());
                });
    }
}
