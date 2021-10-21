package me.bristermitten.warzone.game.spawning.bus;

import io.vavr.collection.Set;
import me.bristermitten.warzone.data.Point;
import me.bristermitten.warzone.game.Game;
import me.bristermitten.warzone.game.spawning.ElytraPlayerSpawner;
import me.bristermitten.warzone.game.spawning.PlayerSpawner;
import me.bristermitten.warzone.party.Party;
import me.bristermitten.warzone.party.PartyManager;
import me.bristermitten.warzone.player.WarzonePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.Objects;

public class BattleBusSpawner implements PlayerSpawner {
    private final ElytraPlayerSpawner elytraPlayerSpawner;
    private final BattleBusFactory battleBusFactory;
    private final BattleBusManager battleBusManager;
    private final PartyManager partyManager;

    @Inject
    public BattleBusSpawner(ElytraPlayerSpawner elytraPlayerSpawner, BattleBusFactory battleBusFactory, BattleBusManager battleBusManager, PartyManager partyManager) {
        this.elytraPlayerSpawner = elytraPlayerSpawner;
        this.battleBusFactory = battleBusFactory;
        this.battleBusManager = battleBusManager;
        this.partyManager = partyManager;
    }

    @Override
    public void spawn(Game game, Set<Party> parties) {
        var y = game.getArena().playableArea().center().y();
        var battleBus = battleBusFactory.createBus(
                game.getArena().getWorldOrThrow(),
                game.getArena().playableArea().min().setY(y),
                game.getArena().playableArea().max().setY(y),
                game.getArena().gameConfig().battleBusSpeed());

        parties.flatMap(Party::getAllMembers)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> {
                    player.setInvisible(true);
                    player.teleport(battleBus.busEntity());
                    battleBus.busEntity().addPassenger(player);
                });

        battleBusManager.start(battleBus);
    }

    @Override
    public void spawn(Game game, Party party) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("BattleBus spawning must spawn all players at once");
    }

    @Override
    public void spawn(Game game, WarzonePlayer player) {
        Point spawnLocation;
        var party = partyManager.getParty(player);
        if (party.isSingle()) {
            spawnLocation = game.getArena().playableArea().random();
        } else {
            // take an average of the party members' locations
            var otherLocations = party.getAllMembers().remove(player.getPlayerId())
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .map(Player::getLocation)
                    .filter(loc -> loc.getWorld().equals(game.getArena().getWorldOrThrow()))
                    .map(Point::fromLocation);

            var x = otherLocations.map(Point::x).average().map(Double::intValue)
                    .getOrElse(() -> game.getArena().playableArea().center().x());

            var z = otherLocations.map(Point::z).average().map(Double::intValue)
                    .getOrElse(() -> game.getArena().playableArea().center().z());

            spawnLocation = new Point(x, Integer.MAX_VALUE, z); // y is completely arbitrary, elytra spawning will take care of it
        }
        elytraPlayerSpawner.spawn(game, player, spawnLocation);
    }
}
