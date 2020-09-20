package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.content.actions.shop.ActionShopFields;
import nl.timvandijkhuizen.commerce.menu.content.actions.shop.ActionShopPayment;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopGateway implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Cart " + Icon.ARROW_RIGHT + " Gateways", 3, 7, 1, 1, 2, 5, 6);
        OrderService orderService = Commerce.getInstance().getService("orders");
        
        // Add gateways buttons
        Set<Gateway> gateways = args.get(0);
        Order cart = args.get(1);
        
        for (Gateway gateway : gateways) {
            MenuItemBuilder item = new MenuItemBuilder(gateway.getType().getIcon());
            TypedValue<String> actionLore = new TypedValue<>();
            
            item.setName(UI.color(gateway.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            item.setLore(() -> {
            	List<String> lore = new ArrayList<>();
            	
            	if(actionLore.get() != null) {
            		lore.add(actionLore.get());
            		return lore;
            	}
            	
            	if(cart.getGateway() != null && cart.getGateway().equals(gateway)) {
            		lore.add(UI.color("Selected", UI.COLOR_SECONDARY));
            	} else {
            		lore.add("");
            		lore.add(UI.color("Left-click to pay using this gateway.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            	}
            		
        		return lore;
            });
            
            // Set click listener
            item.setClickListener(event -> {
                cart.setGateway(gateway);
                
                UI.playSound(player, UI.SOUND_CLICK);
                actionLore.set(UI.color("Saving...", UI.COLOR_TEXT));
                menu.disableButtons();
                menu.refresh();
                
                orderService.saveOrder(cart, success -> {
                    if(success) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        actionLore.set(null);
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        actionLore.set(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                    }
                    
                    menu.enableButtons();
                    menu.refresh();
                });
            });
            
            menu.addPagedButton(item);
        }
        
        // Previous (cart) button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.MINECART);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Fields", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopFields());

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Cart button
        menu.setButton(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 3);
        
        // Next (payment) button
        MenuItemBuilder nextButton = new MenuItemBuilder(Material.DIAMOND);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        nextButton.setLore(UI.color("Payments", UI.COLOR_TEXT));
        nextButton.setClickListener(new ActionShopPayment());

        menu.setButton(nextButton, menu.getSize().getSlots() - 1);
        
        return menu;
    }

}
