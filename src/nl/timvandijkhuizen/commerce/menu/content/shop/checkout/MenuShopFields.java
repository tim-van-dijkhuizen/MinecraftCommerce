package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.Field;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.content.actions.shop.ActionShopGateways;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuShopFields implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Cart " + Icon.ARROW_RIGHT + " Fields", 3, 7, 1, 1, 2, 5, 6);
        OrderService orderService = Commerce.getInstance().getService("orders");
         
        // Get cart & fields
        Order cart = orderService.getCart(player);
        OrderFieldData fieldData = cart.getFieldData();
        
        // Add field buttons
        Set<Field> fields = args.get(0);
        
        for (Field field : fields) {
            MenuItemBuilder item = new MenuItemBuilder(field.getIcon());
            TypedValue<String> actionLore = new TypedValue<>();
            ConfigOption option = field.getOption();
            
            // Get meta and description
            DataArguments meta = option.getMeta();
            String description = meta.getString(0);
            
            item.setName(UI.color(field.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            item.setLore(() -> {
                List<String> lore = new ArrayList<>();
                
                // Return action lore if not null
                if(actionLore.get() != null) {
                    lore.add(actionLore.get());
                    return lore;
                }
                
                lore.add(UI.color(description, UI.COLOR_TEXT));
                lore.add("");
                
                // Create lore
                if(!option.isValueEmpty(fieldData)) {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color(option.getValueLore(fieldData), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }
                
                // Add validation errors to lore
                if (cart.hasErrors(option.getPath())) {
                    lore.add("");
                    lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));

                    for (String error : cart.getErrors(option.getPath())) {
                        lore.add(UI.color(" - " + error, UI.COLOR_ERROR));
                    }
                }
                
                lore.add("");
                lore.add(UI.color("Left-click to edit this field.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                
                return lore;
            });
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                
                option.getValueInput(player, option.getValue(fieldData), value -> {
                    option.setValue(fieldData, value);
                    
                    actionLore.set(UI.color("Saving...", UI.COLOR_TEXT));
                    menu.disableButtons();
                    menu.open(player);
                    
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
            });
            
            menu.addPagedButton(item);
        }
        
        // Previous (cart) button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.MINECART);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Cart", UI.COLOR_TEXT));
        
        previousButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_CART.open(player);
        });

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Cart button
        menu.setButton(ShopHelper.createCartItem(player), menu.getSize().getSlots() - 9 + 3);
        
        // Next (gateway) button
        MenuItemBuilder nextButton = new MenuItemBuilder(Material.DIAMOND);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        nextButton.setLore(() -> {
            List<String> lore = new ArrayList<>();
            
            lore.add(UI.color("Gateway", UI.COLOR_TEXT));
            
            if(cart.hasErrors()) {
                lore.add("");
                lore.add(UI.color("Error: One or more fields have invalid values.", UI.COLOR_ERROR));
            }
            
            return lore;
        });
        
        nextButton.setClickListener(event -> {
            cart.setScenario(Order.SCENARIO_FIELDS);
            
            // Check if fields are valid
            if(!cart.isValid()) {
                UI.playSound(player, UI.SOUND_ERROR);
                cart.setScenario(Order.SCENARIO_DEFAULT);
                menu.refresh();
            } else {
                new ActionShopGateways().onClick(event);
            }
        });

        menu.setButton(nextButton, menu.getSize().getSlots() - 1);
        
        return menu;
    }

}
