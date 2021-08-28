package me.bristermitten.warzone.game.statistic;

import me.bristermitten.warzone.game.GameManager;
import me.bristermitten.warzone.listener.EventListener;
import me.zombie_striker.qg.api.QACustomItemInteractEvent;
import me.zombie_striker.qg.api.QAWeaponDamageEntityEvent;
import me.zombie_striker.qg.api.QAWeaponPrepareShootEvent;
import me.zombie_striker.qg.api.QualityArmory;
import me.zombie_striker.qg.guns.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

import javax.inject.Inject;

public class GameStatisticsListener implements EventListener {
    private final GameManager gameManager;

    @Inject
    GameStatisticsListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onShootHit(QAWeaponDamageEntityEvent event) {
        gameManager.getGameContaining(event.getPlayer().getUniqueId())
                .flatMap(game -> game.getInfo(event.getPlayer().getUniqueId()))
                .peek(info -> info.setShotsHit(info.getShotsHit() + 1));
    }

    @EventHandler
    public void onShoot(QAWeaponPrepareShootEvent event) {
        gameManager.getGameContaining(event.getPlayer().getUniqueId())
                .flatMap(game -> game.getInfo(event.getPlayer().getUniqueId()))
                .peek(info -> info.setShotsFired(info.getShotsFired() + 1));
    }

    @EventHandler
    public void onMedkit(QACustomItemInteractEvent event) {
        if (!event.getCustomItem().getName().equalsIgnoreCase("medkit")) {
            return;
        }
        gameManager.getGameContaining(event.getPlayer().getUniqueId())
                .flatMap(game -> game.getInfo(event.getPlayer().getUniqueId()))
                .peek(info -> info.setMedkitsUsed(info.getMedkitsUsed() + 1));
    }

    @EventHandler
    public void onReload(QACustomItemInteractEvent event) {
        if (!(event.getCustomItem() instanceof Gun gun)) {
            return;
        }
        var reloadingVal = gun.getReloadingingVal();
        if (reloadingVal == null || !reloadingVal.isReloading(event.getPlayer())) {
            return;
        }
        gameManager.getGameContaining(event.getPlayer().getUniqueId())
                .flatMap(game -> game.getInfo(event.getPlayer().getUniqueId()))
                .peek(info -> info.setTimesReloaded(info.getTimesReloaded() + 1));
    }

    @EventHandler
    public void onGunPickUp(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (!QualityArmory.isGun(event.getItem().getItemStack())) {
            return;
        }
        var gun = QualityArmory.getGun(event.getItem().getItemStack());
        gameManager.getGameContaining(player.getUniqueId())
                .flatMap(game -> game.getInfo(player.getUniqueId()))
                .peek(info -> info.getWeaponsPickedUp().add(gun.getName()));
    }

    @EventHandler
    public void onGunPickUpFromInventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!QualityArmory.isGun(event.getCurrentItem())) {
            return;
        }
        var gun = QualityArmory.getGun(event.getCurrentItem());
        gameManager.getGameContaining(player.getUniqueId())
                .flatMap(game -> game.getInfo(player.getUniqueId()))
                .peek(info -> info.getWeaponsPickedUp().add(gun.getName()));
    }
}
