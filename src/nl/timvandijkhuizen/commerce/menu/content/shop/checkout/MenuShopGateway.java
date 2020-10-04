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
        PagedMenu menu = new PagedMenu("Cart " + Icon.ARROW_RIGHT + " Gateways (3/4)", 3, 7, 1, 1, 2, 5, 6);
        OrderService orderService = Commerce.getInstance().getService("orders");
        
        // Add gateways buttons
        Set<Gateway> gateways = args.get(0);
        Order cart = args.get(1);
        
        for (Gateway gateway : gateways) {
            MenuItemBuilder item = new MenuItemBuilder(gateway.getType().getIcon());
            TypedValue<String> actionLore = new TypedValue<>();
            
            item.setName(UI.color(gateway.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            item.setLoreGenerator(() -> {
            	List<String> lore = new ArrayList<>();
            	
            	if(actionLore.get() != null) {
            		lore.add(actionLore.get());
            		return lore;
            	}
            	
        		lore.add("");
        		lore.add(UI.color("Left-click to select this gateway.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            		
        		return lore;
            });
            
            item.setGlowGenerator(() -> gateway.equals(cart.getGateway()));
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                
                if(!gateway.equals(cart.getGateway())) {
                    cart.setGateway(gateway);
                    
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
                }
            });
            
            menu.addPagedButton(item);
        }
        
        // Previous (fields) button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.OAK_SIGN);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Fields", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopFields());

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Cart button
        menu.setButton(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 3);
        
        // Next (payment) button
        MenuItemBuilder nextButton = new MenuItemBuilder(Material.EMERALD);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        
        nextButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();

            lore.add(UI.color("Payment", UI.COLOR_TEXT));
            
            if(!isValid(cart)) {
                lore.add("");
                lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
                
                for(String error : cart.getErrors("gateway")) {
                    lore.add(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
                }
            }
            
            return lore;
        });
        
        nextButton.setClickListener(event -> {
            if(isValid(cart)) {
                new ActionShopPayment().onClick(event);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
            }
        });
        
        menu.setButton(nextButton, menu.getSize().getSlots() - 1);
        
        return menu;
    }
    
    private boolean isValid(Order cart) {
        String oldScenario = cart.getScenario();
        cart.setScenario(Order.SCENARIO_GATEWAYS);
        boolean isValid = cart.isValid();
        cart.setScenario(oldScenario);
        
        return isValid;
    }

}
