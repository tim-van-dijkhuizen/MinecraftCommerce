package nl.timvandijkhuizen.commerce.helpers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopCart;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ShopHelper {

    public static StoreCurrency getBaseCurrency() {
        YamlConfig config = Commerce.getInstance().getConfig();
        ConfigOption<StoreCurrency> option = config.getOption("general.baseCurrency");
        
        return option.getValue(config);
    }
    
    public static float convertPrice(float price, StoreCurrency to) {
        return convertPrice(price, null, to);
    }
    
    public static float convertPrice(float price, StoreCurrency from, StoreCurrency to) {
        float fromRate = from != null ? from.getConversionRate() : 1;
        float basePrice = fromRate == 1 ? price : (price / fromRate);

        return basePrice * to.getConversionRate();
    }
    
    public static String formatPrice(float price) {
        return formatPrice(price, getBaseCurrency());
    }
    
    public static String formatPrice(float price, StoreCurrency to) {
        return formatPrice(price, null, to);
    }
    
    public static String formatPrice(float price, StoreCurrency from, StoreCurrency to) {
        float convertedPrice = convertPrice(price, from, to);
        DecimalFormat format = to.getFormat();
        
        return format.format(convertedPrice);
    }
    
    public static StoreCurrency getCurrencyByCode(String code) {
        StoreCurrency currency = DbHelper.parseCurrency(code);
        
        if(currency == null) {
            YamlConfig config = Commerce.getInstance().getConfig();
            ConfigOption<StoreCurrency> baseCurrencyOption = config.getOption("general.baseCurrency");
            StoreCurrency baseCurrency = baseCurrencyOption.getValue(config);
            
            currency = baseCurrency;
        }
        
        return currency;
    }
    
    public static MenuItemBuilder createCartItem(Order cart) {
        StoreCurrency currency = cart.getCurrency();
        DataList<LineItem> lineItems = cart.getLineItems();

        // Create cart item
        MenuItemBuilder item = new MenuItemBuilder(XMaterial.MINECART);

        item.setName(UI.color("Cart", UI.COLOR_PRIMARY, ChatColor.BOLD));

        item.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();

            lore.add(UI.color("Items:", UI.COLOR_TEXT));

            if (lineItems.size() > 0) {
                for (LineItem lineItem : lineItems) {
                    ProductSnapshot product = lineItem.getProduct();
                    String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                    String price = ShopHelper.formatPrice(lineItem.getPrice(), currency);

                    lore.add(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + " " + UI.color(quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_SECONDARY));
                }
            } else {
                lore.add(UI.TAB + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Add total to lore
            String total = ShopHelper.formatPrice(cart.getTotal(), currency);

            lore.add("");
            lore.add(UI.color("Total: ", UI.COLOR_TEXT) + UI.color(total, UI.COLOR_SECONDARY));
            lore.add("");

            // Add instructions
            lore.add(UI.color("Left-click to view your cart.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            lore.add(UI.color("Right-click to set your currency.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            return lore;
        });

        item.setClickListener(new ActionShopCart());

        return item;
    }

}
