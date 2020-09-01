package nl.timvandijkhuizen.custompayments.menu.content.shop;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.config.sources.UserPreferences;
import nl.timvandijkhuizen.custompayments.services.UserService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCurrency implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        UserService userService = CustomPayments.getInstance().getService("users");
        YamlConfig config = CustomPayments.getInstance().getConfig();
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Currency", 3, 7, 1, 1, 1, 5, 7);

        // Get return menu and currency item
        MenuItemClick clickEvent = args[0].as(MenuItemClick.class);
        Menu returnMenu = clickEvent.getMenu();
        MenuItemBuilder cartItem = clickEvent.getItem();
        
        // Get available currencies
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);

        // Get selected currency
        UserPreferences preferences = userService.getPreferences(player);
        ConfigOption<StoreCurrency> optionCurrency = preferences.getOption("currency");
        StoreCurrency selected = optionCurrency.getValue(preferences);
        
        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(Material.SUNFLOWER);

            // Set category name
            item.setName(UI.color(currency.getCode(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            item.addLore("", UI.color("Use left-click to select.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Add glow if selected
            if(currency.equals(selected)) {
                item.addEnchantGlow();
            }
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.CLICK_SOUND);
                
                // Set option
                optionCurrency.setValue(preferences, currency);
                
                // Update UI
                item.setLore(UI.color("Saving...", UI.TEXT_COLOR));
                menu.refresh();

                // Save product
                userService.savePreferences(player, preferences, success -> {
                    if (success) {
                        UI.playSound(player, UI.SUCCESS_SOUND);
                        cartItem.setLore(UI.color("Currency: ", UI.TEXT_COLOR) + UI.color(currency.getCode(), UI.SECONDARY_COLOR), 0);
                        returnMenu.open(player);
                    } else {
                        UI.playSound(player, UI.ERROR_SOUND);
                        item.setLore(UI.color("Failed to save preferences.", UI.ERROR_COLOR));
                        menu.refresh();
                    }
                });
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            returnMenu.open(player);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
