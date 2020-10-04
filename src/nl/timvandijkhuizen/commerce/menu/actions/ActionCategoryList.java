package nl.timvandijkhuizen.commerce.menu.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionCategoryList implements MenuItemAction {

    private boolean clickSound = true;
    
    public ActionCategoryList(boolean clickSound) {
        this.clickSound = clickSound;
    }
    
    public ActionCategoryList() { }
    
    @Override
    public void onClick(MenuItemClick event) {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        if(clickSound) {
            UI.playSound(whoClicked, UI.SOUND_CLICK);
        }
        
        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();

        // Create menu
        categoryService.getCategories(categories -> {
            activeMenu.enableButtons();
            
            if (categories == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load categories.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.CATEGORY_LIST.open(whoClicked, categories);
        });
    }

}
