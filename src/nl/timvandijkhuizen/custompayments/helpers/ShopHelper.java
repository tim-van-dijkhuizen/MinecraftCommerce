package nl.timvandijkhuizen.custompayments.helpers;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.UserService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ShopHelper {

    public static final MenuItemBuilder CART_BUTTON = new MenuItemBuilder(Material.MINECART)
        .setName(UI.color("Cart", UI.PRIMARY_COLOR, ChatColor.BOLD))
        .setLore("", "")
        .addLore(UI.color("Use left-click to view your cart.", UI.SECONDARY_COLOR, ChatColor.ITALIC))
        .addLore(UI.color("Use right-click to set your currency.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
    
    public static String localize(float price, StoreCurrency currency) {
        DecimalFormat format = currency.getFormat();
        return format.format(price *= currency.getConversionRate());
    }
    
    public static String localize(float price) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<StoreCurrency> option = config.getOption("general.baseCurrency");
        StoreCurrency baseCurrency = option.getValue(config);
        
        return localize(price, baseCurrency);
    }
    
    public static MenuItemBuilder createCartItem(Player player, Menu menu) {
        UserService userService = CustomPayments.getInstance().getService("users");
        MenuItemBuilder item = CART_BUTTON.clone();

        // Set lore to selected currency
        UserPreferences preferences = userService.getPreferences(player);
        ConfigOption<StoreCurrency> optionCurrency = preferences.getOption("currency");
        StoreCurrency currency = optionCurrency.getValue(preferences);
        
        item.setLore(UI.color("Currency: ", UI.TEXT_COLOR) + UI.color(currency.getCode(), UI.SECONDARY_COLOR), 0);
        
        // Set click listener
        item.setClickListener(event -> {
            ClickType type = event.getClickType();
            
            UI.playSound(player, UI.CLICK_SOUND);
            
            if(type == ClickType.LEFT) {
                
            } else if(type == ClickType.RIGHT) {
                Menus.SHOP_CURRENCY.open(player, event);
            }
        });
        
        return item;
    }
    
}
