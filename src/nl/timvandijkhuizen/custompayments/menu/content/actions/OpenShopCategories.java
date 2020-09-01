package nl.timvandijkhuizen.custompayments.menu.content.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenShopCategories implements MenuAction {

    @Override
    public void onClick(MenuItemClick event) {
        CategoryService categoryService = CustomPayments.getInstance().getService("categories");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.CLICK_SOUND);
        
        clickedItem.setLore(UI.color("Loading...", UI.TEXT_COLOR));
        activeMenu.refresh();

        // Create menu
        categoryService.getCategories(categories -> {
            if (categories == null) {
                UI.playSound(whoClicked, UI.ERROR_SOUND);
                clickedItem.setLore(UI.color("Error: Failed to load categories.", UI.ERROR_COLOR));
                activeMenu.refresh();
            }

            Menus.SHOP_CATEGORIES.open(whoClicked, categories);
        });
    }

}
