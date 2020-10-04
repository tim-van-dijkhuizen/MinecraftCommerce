package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.content.actions.shop.ActionShopGateways;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.commerce.services.PaymentService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopPayment implements PredefinedMenu {

	@Override
	public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Cart " + Icon.ARROW_RIGHT + " Payment (4/4)", MenuSize.LG);
        OrderService orderService = Commerce.getInstance().getService("orders");
        PaymentService paymentService = Commerce.getInstance().getService("payments");
        TypedValue<Boolean> accepted = new TypedValue<>(false);

        // Get cart
        Order cart = args.get(0);
        Gateway gateway = cart.getGateway();
        
        // Validate cart
        String oldScenario = cart.getScenario();
        cart.setScenario(Order.SCENARIO_PAY);
        boolean cartValid = cart.isValid();
        cart.setScenario(oldScenario);
        
        // Pay button
        MenuItemBuilder acceptButton = new MenuItemBuilder(Material.BOOK);

        acceptButton.setName(UI.color("Terms & Conditions", UI.COLOR_PRIMARY, ChatColor.BOLD));
        
        acceptButton.addLore("");
        acceptButton.addLore(UI.color("Use left-click to accept the terms & conditions.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        acceptButton.addLore(UI.color("Use right-click to view the terms & conditions.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        acceptButton.setClickListener(event -> {
        	ClickType type = event.getClickType();
        	
        	UI.playSound(player, UI.SOUND_CLICK);
        	
        	if(type == ClickType.LEFT) {
        		accepted.set(!accepted.get());
            	
            	if(accepted.get()) {
            		acceptButton.addEnchantGlow();
            	} else {
            		acceptButton.removeEnchantGlow();
            	}
            	
            	menu.refresh();
        	} else if(type == ClickType.RIGHT) {
        		
        	}
        });

        menu.setButton(acceptButton, 11);
        
        // Pay button
        MenuItemBuilder payButton = new MenuItemBuilder(Material.COMPARATOR);
        TypedValue<String> payActionLore = new TypedValue<>();
        String gatewayName = gateway != null ? gateway.getDisplayName() : "";

        payButton.setName(UI.color("Pay using " + gatewayName, UI.COLOR_PRIMARY, ChatColor.BOLD));
        
        payButton.setLoreGenerator(() -> {
        	List<String> lore = new ArrayList<>();
        	
        	if(payActionLore.get() != null) {
        	    lore.add(payActionLore.get());
        	} else if (!cartValid) {
        	    lore.add("");
        	    lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));
        	    
        	    for(String error : cart.getErrors()) {
        	        lore.add(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
        	    }
        	} else if(!accepted.get()) {
        		lore.add(UI.color("You must accept the terms & conditions.", UI.COLOR_ERROR));
        	}
        	
        	return lore;
        });

        payButton.setClickListener(event -> {
            if(cartValid && accepted.get()) {
                String cachedUrl = cart.getPaymentUrl();
                
                // Return cached URL if we've got one
                if(cachedUrl != null) {
                    sendPaymentUrl(player, menu, cachedUrl);
                    return;
                }
                
                // Create URL and cache it
                UI.playSound(player, UI.SOUND_CLICK);
                payActionLore.set(UI.color("Loading...", UI.COLOR_TEXT));
                menu.disableButtons();
                menu.refresh();
                
                paymentService.createPaymentUrl(cart, url -> {
                	if(url != null) {
                        cart.setPaymentUrl(url);
                        
                        // Save cart
                        orderService.saveOrder(cart, success -> {
                            menu.enableButtons();
                            
                            if(success) {
                                payActionLore.set(null);
                                sendPaymentUrl(player, menu, url);
                            } else {
                                UI.playSound(player, UI.SOUND_ERROR);
                                payActionLore.set(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                                menu.refresh();
                            }
                        });
                	} else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        payActionLore.set(UI.color("Failed to create payment url.", UI.COLOR_ERROR));
                        menu.enableButtons();
                        menu.refresh();
                	}
                });
            } else {
            	UI.playSound(player, UI.SOUND_ERROR);
            }
        });

        menu.setButton(payButton, 15);

        // Cart button
        menu.setButton(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 4);
        
        // Previous (gateway) button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.OAK_FENCE_GATE);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Gateways", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopGateways());

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Set bottom line
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);
        
        return menu;
	}
	
	private void sendPaymentUrl(Player player, Menu menu, String url) {
	    UI.playSound(player, UI.SOUND_SUCCESS);
        menu.close(player);
        
        // Send message
        player.sendMessage(UI.color(StringUtils.repeat(Icon.SQUARE, 75), UI.COLOR_TEXT, ChatColor.BOLD));
        player.sendMessage("");
        player.sendMessage(UI.color("Complete your order by paying using this link:", UI.COLOR_PRIMARY, ChatColor.BOLD));
        player.sendMessage(UI.color(url, UI.COLOR_SECONDARY));
        player.sendMessage("");
        player.sendMessage(UI.color(StringUtils.repeat(Icon.SQUARE, 75), UI.COLOR_TEXT, ChatColor.BOLD));
	}

}
