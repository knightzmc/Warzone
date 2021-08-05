package me.bristermitten.warzone.game;

import me.bristermitten.warzone.arena.Arena;
import me.bristermitten.warzone.loot.LootGenerator;
import me.zombie_striker.qg.api.QualityArmory;

public class GameChestFiller {
    private final LootGenerator generator;

    public GameChestFiller(LootGenerator generator) {
        this.generator = generator;
    }

    public void fill(Arena arena) {
        QualityArmory.getGuns().forEachRemaining(g -> g.getName());
    }
}
