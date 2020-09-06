package nl.timvandijkhuizen.custompayments.menu.content.shop;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenShopCategories;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopProducts implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Category category = args[0].as(Category.class);
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " " + category.getName(), 3, 7, 1, 1, 1, 5, 7);

        // Create cart item
        MenuItemBuilder cartItem = ShopHelper.createCartItem(player);
        
        // Add product buttons
        List<Product> products = args[1].asList(Product.class);

        for (Product product : products) {
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon());
            
            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }
            
            item.addLore(UI.color("Price: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_SECONDARY), "");

            // Set click listener
            item.setClickListener(event -> {
                OrderService orderService = CustomPayments.getInstance().getService("orders");
                Order cart = orderService.getCart(player);
                List<String> lore = new ArrayList<>(item.getLore());
                
                UI.playSound(player, UI.SOUND_CLICK);
                item.setLore(UI.color("Saving...", UI.COLOR_TEXT));
                menu.disableButtons();
                menu.refresh();
                
                // Add item
                cart.addLineItem(new LineItem(product, 1));
                
                orderService.saveOrder(cart, success -> {
                    if(success) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        item.setLore(lore);
                        ShopHelper.updateCartItem(cartItem, player);
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        item.setLore(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                    }
                    
                    menu.enableButtons();
                    menu.refresh();
                });
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(new OpenShopCategories());

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Currency button
        menu.setButton(cartItem, menu.getSize().getSlots() - 9 + 4);
        
        return menu;
    }

}
