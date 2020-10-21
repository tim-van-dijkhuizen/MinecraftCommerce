package nl.timvandijkhuizen.commerce.menu.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionOrderList implements MenuItemAction {

    private boolean clickSound = true;

    public ActionOrderList(boolean clickSound) {
        this.clickSound = clickSound;
    }

    public ActionOrderList() {
    }

    @Override
    public void onClick(MenuItemClick event) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        if (clickSound) {
            UI.playSound(whoClicked, UI.SOUND_CLICK);
        }

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();

        // Create menu
        orderService.getOrders(orders -> {
            activeMenu.enableButtons();

            if (orders == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load orders.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.ORDER_LIST.open(whoClicked, orders);
        });
    }

}