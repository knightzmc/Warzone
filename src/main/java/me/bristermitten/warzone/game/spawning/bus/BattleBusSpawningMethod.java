package me.bristermitten.warzone.game.spawning.bus;

import io.vavr.collection.Set;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.spawning.ElytraPlayerSpawner;
import me.bristermitten.warzone.game.spawning.PlayerSpawner;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.player.WarzonePlayer;

import javax.inject.Inject;

public class BattleBusSpawningMethod implements PlayerSpawner {
    private final ElytraPlayerSpawner elytraPlayerSpawner;
    private final BattleBusFactory battleBusFactory;
    private final BattleBusManager battleBusManager;

    @Inject
    public BattleBusSpawningMethod(ElytraPlayerSpawner elytraPlayerSpawner, BattleBusFactory battleBusFactory, BattleBusManager battleBusManager) {
        this.elytraPlayerSpawner = elytraPlayerSpawner;
        this.battleBusFactory = battleBusFactory;
        this.battleBusManager = battleBusManager;
    }

    @Override
    public void spawn(Game game, Set<Party> parties) {
        //TODO
//        var battleBus = battleBusFactory.createBus()
    }

    @Override
    public void spawn(Game game, Party party) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("BattleBus spawning must spawn all players at once");
    }

    @Override
    public void spawn(Game game, WarzonePlayer player) {
        // TODO how should this be handled?
        elytraPlayerSpawner.spawn(game, player);
    }
}