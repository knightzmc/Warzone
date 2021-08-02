package me.bristermitten.warzone.leaderboard;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.bristermitten.warzone.leaderboard.LeaderboardMenu;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@CommandAlias("leaderboard|lb")
public class LeaderboardCommand extends BaseCommand {
    private final LeaderboardMenu leaderboardMenu;

    @Inject
    public LeaderboardCommand(LeaderboardMenu leaderboardMenu) {
        this.leaderboardMenu = leaderboardMenu;
    }

    @Default
    public void run(Player player) {
        leaderboardMenu.open(player);
    }
}
