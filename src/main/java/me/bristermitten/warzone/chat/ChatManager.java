package me.bristermitten.warzone.chat;

import me.bristermitten.warzone.chat.channel.ChatChannel;
import org.bukkit.entity.Player;

public interface ChatManager {

    void sendMessage(ChatChannel chatChannel, String message, Player sender);
}
