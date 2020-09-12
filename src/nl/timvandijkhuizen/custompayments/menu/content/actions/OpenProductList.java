package nl.timvandijkhuizen.custompayments.menu.content.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenProductList implements MenuAction {

    private boolean clickSound = true;
    
    public OpenProductList(boolean clickSound) {
        this.clickSound = clickSound;
    }
    
    public OpenProductList() { }
    
    @Override
    public void onClick(MenuItemClick event) {
        ProductService productService = CustomPayments.getInstance().getService("products");
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
