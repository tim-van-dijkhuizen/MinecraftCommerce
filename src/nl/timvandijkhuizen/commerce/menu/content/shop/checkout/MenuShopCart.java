package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCart implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Cart", 3, 7, 1, 1, 2, 5, 6);
        Order cart = orderService.getCart(player);

        // Create cart item
        MenuItemBuilder cartItem = ShopHelper.createCartItem(player);
        
        for (LineItem lineItem : cart.getLineItems()) {
            ProductSnapshot product = lineItem.getProduct();
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon(), lineItem.getQuantity());
            
            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }
            
            // Category and price
            item.addLore("", UI.color("Category: ", UI.COLOR_TEXT) + UI.color(product.getCategoryName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Price: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(product.getPrice(), cart.getCurrency()), UI.COLOR_SECONDARY), "");
            
            item.addLore("", UI.color("Use left-click to increase the quantity.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to decrease the quantity.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            item.setClickListener(event -> {
               ClickType type = event.getClickType();
               List<String> oldLore = item.getLore();
               
               UI.playSound(player, UI.SOUND_CLICK);
               item.setLore(UI.color("Saving...", UI.COLOR_TEXT));
               menu.refresh();
               menu.disableButtons();
               
               // Update LineItem
               if(type == ClickType.LEFT) {
                   lineItem.setQuantity(lineItem.getQuantity() + 1);
               } else if(type == ClickType.RIGHT) {
                   lineItem.setQuantity(lineItem.getQuantity() - 1);
               }
               
               // Save cart
               orderService.saveOrder(cart, success -> {
                   menu.enableButtons();
                   
                   if(success) {
                       UI.playSound(player, UI.SOUND_SUCCESS);
                       
                       if(lineItem.getQuantity() > 0) {
                           item.setAmount(lineItem.getQuantity());
                           item.setLore(oldLore);
                       } else {
                           menu.removePagedButton(item);
                       }
                       
                       menu.refresh();
                   } else {
                       UI.playSound(player, UI.SOUND_ERROR);
                       item.setLore(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                       menu.refresh();
                   }
               });
            });

            menu.addPagedButton(item);
        }

        // Cart button
        menu.setButton(cartItem, menu.getSize().getSlots() - 9 + 3);

        // Previous button
        MenuItemBuilder previousButton = new MenuItemBuilder(Material.RED_BED);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Shop Home", UI.COLOR_TEXT));
        
        previousButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_CATEGORIES.open(player);
        });

        menu.setButton(previousButton, menu.getSize().getSlots() - 9);
        
        // Next (fields) button
        MenuItemBuilder nextButton = new MenuItemBuilder(Material.OAK_SIGN);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        nextButton.setLore(UI.color("Fields", UI.COLOR_TEXT));
        
        nextButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_FIELDS.open(player);
        });

        menu.setButton(nextButton, menu.getSize().getSlots() - 1);
        
        return menu;
    }

}
