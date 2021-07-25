package me.bristermitten.warzone.scoreboard;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record ScoreboardTemplate(String title, @Unmodifiable List<String> lines) {
}
