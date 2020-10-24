package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopOrderHistory implements MenuItemAction {

    @Override
    public void onClick(MenuItemClick event) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();
        
        orderService.getOrdersByPlayer(whoClicked.getUniqueId(), orders -> {
            activeMenu.enableButtons();

            if (orders != null) {
                Menus.SHOP_ACCOUNT_ORDERS.open(whoClicked, orders);
            } else {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load orders.", UI.COLOR_ERROR));
                activeMenu.refresh();
            }
        });
    }

}
