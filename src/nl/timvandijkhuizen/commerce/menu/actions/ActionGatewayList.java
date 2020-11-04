package nl.timvandijkhuizen.commerce.menu.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionGatewayList implements MenuClickListener {

    private boolean clickSound = true;

    public ActionGatewayList(boolean clickSound) {
        this.clickSound = clickSound;
    }

    public ActionGatewayList() {
    }

    @Override
    public void onClick(MenuClick event) {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        if (clickSound) {
            UI.playSound(whoClicked, UI.SOUND_CLICK);
        }

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableItems();
        activeMenu.refresh();

        // Create menu
        gatewayService.getGateways(gateways -> {
            activeMenu.enableItems();

            if (gateways == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load gateways.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.GATEWAY_LIST.open(whoClicked, gateways);
        });
    }

}
