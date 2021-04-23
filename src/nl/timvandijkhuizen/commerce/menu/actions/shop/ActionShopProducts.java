package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopProducts implements MenuClickListener {

    private Category category;

    public ActionShopProducts(Category category) {
        this.category = category;
    }

    @Override
    public void onClick(MenuClick event) {
        ProductService productService = Commerce.getInstance().getService(ProductService.class);
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableItems();
        activeMenu.refresh();

        // Create menu
        productService.getProducts(category, categories -> {
            if (categories == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load categories.", UI.COLOR_ERROR));
                activeMenu.enableItems();
                activeMenu.refresh();
                return;
            }

            orderService.getCart(whoClicked, cart -> {
                if (cart == null) {
                    UI.playSound(whoClicked, UI.SOUND_ERROR);
                    clickedItem.setLore(UI.color("Error: Failed to load cart.", UI.COLOR_ERROR));
                    activeMenu.enableItems();
                    activeMenu.refresh();
                    return;
                }

                Menus.SHOP_PRODUCTS.open(whoClicked, category, categories, cart);
            });
        });
    }

}
