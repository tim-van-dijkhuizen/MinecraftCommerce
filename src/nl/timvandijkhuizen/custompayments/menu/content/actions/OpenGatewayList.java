package nl.timvandijkhuizen.custompayments.menu.content.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuAction;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class OpenGatewayList implements MenuAction {

    private boolean clickSound = true;
    
    public OpenGatewayList(boolean clickSound) {
        this.clickSound = clickSound;
    }
    
    public OpenGatewayList() { }
    
    @Override
    public void onClick(MenuItemClick event) {
        GatewayService gatewayService = CustomPayments.getInstance().getService("gateways");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        if(clickSound) {
            UI.playSound(whoClicked, UI.CLICK_SOUND);
        }
        
        clickedItem.setLore(UI.color("Loading...", UI.TEXT_COLOR));
        activeMenu.refresh();

        // Create menu
        gatewayService.getGateways(gateways -> {
            if (gateways == null) {
                UI.playSound(whoClicked, UI.ERROR_SOUND);
                clickedItem.setLore(UI.color("Error: Failed to load gateways.", UI.ERROR_COLOR));
                activeMenu.refresh();
            }

            Menus.GATEWAY_LIST.open(whoClicked, gateways);
        });
    }

}
