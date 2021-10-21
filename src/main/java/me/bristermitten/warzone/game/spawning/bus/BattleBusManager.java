package me.bristermitten.warzone.game.spawning.bus;

import javax.inject.Inject;

public class BattleBusManager {
    private final BattleBusMoveTask battleBusMoveTask;

    @Inject
    public BattleBusManager(BattleBusMoveTask battleBusMoveTask) {
        this.battleBusMoveTask = battleBusMoveTask;
    }

    public void start(BattleBus battleBus) {
        battleBusMoveTask.add(battleBus);
        battleBusMoveTask.setPaused(battleBus, false);
        battleBusMoveTask.start();
    }

    public void stop(BattleBus battleBus) {
        battleBusMoveTask.setPaused(battleBus, true);
    }
}
