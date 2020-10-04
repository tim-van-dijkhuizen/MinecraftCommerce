package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopGateways implements MenuItemAction {

    @Override
    public void onClick(MenuItemClick event) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);
        
        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();

        // Create menu
        gatewayService.getGateways(gateways -> {
            if (gateways == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load gateways.", UI.COLOR_ERROR));
                activeMenu.enableButtons();
                activeMenu.refresh();
                return;
            }

            orderService.getCart(whoClicked, cart -> {
                if (cart == null) {
                    UI.playSound(whoClicked, UI.SOUND_ERROR);
                    clickedItem.setLore(UI.color("Error: Failed to load cart.", UI.COLOR_ERROR));
                    activeMenu.enableButtons();
                    activeMenu.refresh();
                    return;
                }
                
                Menus.SHOP_GATEWAY.open(whoClicked, gateways, cart);
            });
        });
    }
    
}