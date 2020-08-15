package nl.timvandijkhuizen.custompayments.menu.content.actions;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClickEvent;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenCategoryList implements MenuAction {

    @Override
    public void onClick(MenuItemClickEvent event) {
        CategoryService categoryService = CustomPayments.getInstance().getService("categories");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
        clickedItem.setLore(UI.color("Loading...", UI.TEXT_COLOR));
        activeMenu.setButton(clickedItem, 15);

        // Create menu
        categoryService.getCategories(categories -> {
            if (categories == null) {
                clickedItem.setLore(UI.color("Error: Failed to load categories.", UI.ERROR_COLOR));
                activeMenu.setButton(clickedItem, 15);
            }

            Menus.CATEGORY_LIST.open(whoClicked, categories);
        });
    }

}
