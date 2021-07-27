package me.bristermitten.warzone.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("warzone")
public class WarzoneCommand extends BaseCommand {
    @Subcommand("join")
    public void join(Player sender) {
        throw new UnsupportedOperationException("TODO");
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        throw new UnsupportedOperationException("TODO");
    }
}
