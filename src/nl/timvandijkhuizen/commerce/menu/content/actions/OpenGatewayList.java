package nl.timvandijkhuizen.commerce.menu.content.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenGatewayList implements MenuAction {

    private boolean clickSound = true;
    
    public OpenGatewayList(boolean clickSound) {
        this.clickSound = clickSound;
    }
    
    public OpenGatewayList() { }
    
    @Override
    public void onClick(MenuItemClick event) {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
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
        gatewayService.getGateways(gateways -> {
            activeMenu.enableButtons();
            
            if (gateways == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load gateways.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.GATEWAY_LIST.open(whoClicked, gateways);
        });
    }

}
