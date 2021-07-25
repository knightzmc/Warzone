package me.bristermitten.warzone.scoreboard;

import io.vavr.Function2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import me.bristermitten.warzone.chat.ChatManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.regex.Pattern;

public record ScoreboardRenderer(ChatManager chatManager) {
    private static final Pattern LINES = Pattern.compile("\n");

    @Inject
    public ScoreboardRenderer {
    }

    private List<String> renderLines(List<String> lines, Player player) {
        return lines
                .map(Function2.of(chatManager::format).reversed().apply(player)) // Apply placeholders initially, might generate new lines
                .flatMap(line -> List.of(LINES.split(line)))
                .map(Function2.of(chatManager::format).reversed().apply(player)); // Apply placeholders again
    }


    public void show(@NotNull ScoreboardTemplate template, @NotNull Player player) {
        var board = player.getScoreboard();
        var title = chatManager.format(template.title(), player);

        Objective objective = Option.of(board.getObjective("Warzone"))
                .getOrElse(() -> board.registerNewObjective("Warzone", "dummy", title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(title);

        updateScores(template, objective, player);
    }

    private void updateScores(@NotNull ScoreboardTemplate template, @NotNull Objective objective, @NotNull Player player) {
        var lines = renderLines(List.ofAll(template.lines()), player);
        lines.zipWithIndex()
                .forEach(tuple -> tuple.apply((line, index) -> {
                    objective.getScore(line).setScore(15 - index);
                    return null; // not really relevant
                }));

    }
}
