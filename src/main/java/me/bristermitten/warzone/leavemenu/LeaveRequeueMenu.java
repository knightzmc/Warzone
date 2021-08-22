package me.bristermitten.warzone.leavemenu;

import com.google.inject.assistedinject.Assisted;
import me.bristermitten.warzone.config.Configuration;
import me.bristermitten.warzone.menu.Menu;
import me.bristermitten.warzone.menu.MenuAction;
import me.bristermitten.warzone.menu.MenuListener;
import me.bristermitten.warzone.menu.MenuRenderer;
import me.bristermitten.warzone.party.PartyManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.inject.Inject;

public class LeaveRequeueMenu implements Menu {
    public static final Configuration<LeaveRequeueMenuConfig> CONFIG = new Configuration<>(LeaveRequeueMenuConfig.class, "menus/leave-party.yml");

    private final Runnable action;
    private final String actionName;
    private final MenuRenderer renderer;
    private final Plugin plugin;
    private final PartyManager partyManager;
    private final LeaveRequeueMenuLoader loader;

    @Inject
    LeaveRequeueMenu(@Assisted Runnable action, @Assisted String actionName, MenuRenderer renderer, Plugin plugin, PartyManager partyManager, LeaveRequeueMenuLoader loader) {
        this.action = action;
        this.actionName = actionName;
        this.renderer = renderer;
        this.plugin = plugin;
        this.partyManager = partyManager;
        this.loader = loader;
    }

    @Override
    public void open(Player player) {
        var page = loader.get();
        var inv = renderer.render(page, player,
                (message, p) -> message.replace("{action}", actionName));

        new MenuListener(inv, event -> {
            var clicked = page.items().get(event.getSlot());
            event.setCancelled(true);
            if (clicked == null || clicked.action() == null) {
                return;
            }

            if (clicked.action() == MenuAction.LEAVE_PARTY_ACTION) {
                partyManager.leave(partyManager.getParty(player), player);
                action.run();
                return;
            }
            if (clicked.action() == MenuAction.BRING_PARTY_ACTION) {
                action.run();
                return;
            }
            clicked.action().execute(event);
        }).register(plugin);

        player.openInventory(inv);
    }

    @Override
    public void close(Player player) {
        player.closeInventory();
    }
}
