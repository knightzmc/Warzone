package me.bristermitten.warzone.scoreboard;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import me.bristermitten.warzone.chat.ChatFormatter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Objects;
import java.util.regex.Pattern;

public record ScoreboardRenderer(ChatFormatter chatManager) {
    private static final Pattern LINES = Pattern.compile("\n");

    @Inject
    public ScoreboardRenderer {
    }


    private List<Component> renderLines(@NotNull List<String> lines, Player player) {
        return lines
                .map(Function2.of(chatManager::preFormat).reversed().apply(player)) // Apply placeholders initially, might generate new lines
                .flatMap(line -> List.of(LINES.split(line)))
                .map(Function2.of(chatManager::format).reversed().apply(player));
    }


    void show(@NotNull ScoreboardTemplate template, @NotNull Player player, @NotNull Scoreboard board) {
        player.setScoreboard(board);
        var title = chatManager.format(template.title(), player);
        Objective objective = Option.of(board.getObjective("Warzone"))
                .getOrElse(() -> board.registerNewObjective("Warzone", "dummy", title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.displayName(title);

        for (int i = 0; i < 15; i++) {
            var teamName = "team" + i;
            var team = board.getTeam(teamName);
            if (team == null) {
                team = board.registerNewTeam(teamName);
            }
            team.addEntry(" ".repeat(i));
        }
        updateScores(template, board, objective, player);
    }

    private void updateScores(@NotNull ScoreboardTemplate template,
                              @NotNull Scoreboard board, @NotNull Objective objective, @NotNull Player player) {
        var lines = renderLines(List.ofAll(template.lines()), player);

        for (int index = 0; index < 15; index++) {
            var team = board.getTeam("team" + index);
            Objects.requireNonNull(team, "this should not happen!");
            if (index >= lines.size()) {
                board.resetScores(" ".repeat(index));
            } else {
                team.prefix(lines.get(index));
                objective.getScore(" ".repeat(index)).setScore(15 - index);
            }
        }
    }
}
