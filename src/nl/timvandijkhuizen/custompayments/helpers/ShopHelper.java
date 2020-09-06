package nl.timvandijkhuizen.custompayments.helpers;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.ProductSnapshot;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ShopHelper {
    
    public static String formatPrice(float price, StoreCurrency currency) {
        DecimalFormat format = currency.getFormat();
        return format.format(price *= currency.getConversionRate());
    }
    
    public static String formatPrice(float price) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<StoreCurrency> option = config.getOption("general.baseCurrency");
        StoreCurrency baseCurrency = option.getValue(config);
        
        return formatPrice(price, baseCurrency);
    }
    
    public static MenuItemBuilder createCartItem(Player player) {
        MenuItemBuilder item = new MenuItemBuilder(Material.MINECART);
        
        item.setName(UI.color("Cart", UI.COLOR_PRIMARY, ChatColor.BOLD));
        updateCartItem(item, player);
        
        return item;
    }
    
    public static void updateCartItem(MenuItemBuilder item, Player player) {
        OrderService orderService = CustomPayments.getInstance().getService("orders");
        Order cart = orderService.getCart(player);
        StoreCurrency currency = cart.getCurrency();
        
        // Add items to lore
        DataList<LineItem> lineItems = cart.getLineItems();
        
        item.setLore(UI.color("Items:", UI.COLOR_TEXT));
        
        if (lineItems.size() > 0) {
            for(LineItem lineItem : lineItems) {
                ProductSnapshot product = lineItem.getProduct();
                String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                String price = ShopHelper.formatPrice(product.getPrice(), currency);
                
                item.addLore(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + " " + UI.color(quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_SECONDARY));
            }
        } else {
            item.addLore(UI.TAB + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }
        
        // Add total to lore
        String total = ShopHelper.formatPrice(cart.getTotal(), currency);
        item.addLore("", UI.color("Total: ", UI.COLOR_TEXT) + UI.color(total, UI.COLOR_SECONDARY), "");
        
        // Add instructions
        item.addLore(UI.color("Use left-click to view your cart.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        item.addLore(UI.color("Use right-click to set your currency.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        
        // Set click listener
        item.setClickListener(event -> {
            ClickType type = event.getClickType();
            
            UI.playSound(player, UI.SOUND_CLICK);
            
            if(type == ClickType.LEFT) {
                Menus.SHOP_CART.open(player);
            } else if(type == ClickType.RIGHT) {
                Menus.SHOP_CURRENCY.open(player, event);
            }
        });
    }
    
}
