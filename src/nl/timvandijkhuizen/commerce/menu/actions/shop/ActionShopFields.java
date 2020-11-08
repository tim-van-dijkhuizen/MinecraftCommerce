package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopFields implements MenuClickListener {

    @Override
    public void onClick(MenuClick event) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableItems();
        activeMenu.refresh();

        // Create menu
        orderService.getCart(whoClicked, cart -> {
            activeMenu.enableItems();

            if (cart == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load cart.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.SHOP_FIELDS.open(whoClicked, cart);
        });
    }

}