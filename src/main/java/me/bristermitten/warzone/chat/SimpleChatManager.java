package me.bristermitten.warzone.chat;

import io.vavr.collection.List;
import io.vavr.concurrent.Future;
import io.vavr.control.Option;
import me.bristermitten.warzone.chat.channel.ChatChannel;
import me.bristermitten.warzone.player.WarzonePlayer;
import me.bristermitten.warzone.player.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;

public class SimpleChatManager implements ChatManager {
    private final ChatFormatter formatter;
    private final PlayerStorage playerStorage;

    @Inject
    public SimpleChatManager(ChatFormatter formatter, PlayerStorage playerStorage) {
        this.formatter = formatter;
        this.playerStorage = playerStorage;
    }

    @Override
    public void sendMessage(ChatChannel channel, String message, Player sender) {
        Future.sequence(
                List.ofAll(Bukkit.getOnlinePlayers())
                        .map(Player::getUniqueId)
                        .map(playerStorage::load)
        ).onSuccess(allPlayers -> allPlayers
                .filter(player -> player.getCurrentState().getChannel().equals(channel))
                .map(WarzonePlayer::getPlayer)
                .filter(Option::isDefined)
                .map(Option::get)
                .forEach(player -> player.sendMessage(
                        formatter.format(message, sender)
                )));
    }
}
