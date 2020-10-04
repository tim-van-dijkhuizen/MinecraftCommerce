package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopCategories implements MenuItemAction {

    @Override
    public void onClick(MenuItemClick event) {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        OrderService orderService = Commerce.getInstance().getService("orders");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);
        
        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();

        // Create menu
        categoryService.getCategories(categories -> {
            if (categories == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load categories.", UI.COLOR_ERROR));
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
                
                Menus.SHOP_CATEGORIES.open(whoClicked, categories, cart);
            });
        });
    }

}
