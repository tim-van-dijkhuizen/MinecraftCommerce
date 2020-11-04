package nl.timvandijkhuizen.commerce.menu.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionProductList implements MenuClickListener {

    private boolean clickSound = true;

    public ActionProductList(boolean clickSound) {
        this.clickSound = clickSound;
    }

    public ActionProductList() {
    }

    @Override
    public void onClick(MenuClick event) {
        ProductService productService = Commerce.getInstance().getService("products");
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
        productService.getProducts(products -> {
            activeMenu.enableItems();

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
