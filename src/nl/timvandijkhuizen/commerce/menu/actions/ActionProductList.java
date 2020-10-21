package nl.timvandijkhuizen.commerce.menu.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionProductList implements MenuItemAction {

    private boolean clickSound = true;

    public ActionProductList(boolean clickSound) {
        this.clickSound = clickSound;
    }

    public ActionProductList() {
    }

    @Override
    public void onClick(MenuItemClick event) {
        ProductService productService = Commerce.getInstance().getService("products");
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
        productService.getProducts(products -> {
            activeMenu.enableButtons();

            if (products == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load products.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.PRODUCT_LIST.open(whoClicked, products);
        });
    }

}
