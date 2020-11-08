package nl.timvandijkhuizen.commerce.menu.actions.shop;

import java.util.function.Consumer;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopCart implements MenuClickListener {

    @Override
    public void onClick(MenuClick event) {
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();
        ClickType clickType = event.getClickType();

        UI.playSound(whoClicked, UI.SOUND_CLICK);

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableItems();
        activeMenu.refresh();

        if (clickType == ClickType.LEFT) {
            loadCart(event, cart -> Menus.SHOP_CART.open(whoClicked, cart));
        } else if (clickType == ClickType.RIGHT) {
            loadCart(event, cart -> Menus.SHOP_CURRENCY.open(whoClicked, cart, activeMenu));
        }
    }

    private void loadCart(MenuClick event, Consumer<Order> callback) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        orderService.getCart(whoClicked, cart -> {
            activeMenu.enableItems();

            if (cart != null) {
                callback.accept(cart);
            } else {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load cart.", UI.COLOR_ERROR));
                activeMenu.refresh();
            }
        });
    }

}
