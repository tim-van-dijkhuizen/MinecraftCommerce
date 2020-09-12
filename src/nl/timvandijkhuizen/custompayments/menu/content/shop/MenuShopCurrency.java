package nl.timvandijkhuizen.custompayments.menu.content.shop;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.config.objects.StoreCurrency;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCurrency implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Currency", 3, 7, 1, 1, 1, 5, 7);
        OrderService orderService = CustomPayments.getInstance().getService("orders");
        Order cart = orderService.getCart(player);

        // Get return menu and currency item
        MenuItemClick clickEvent = args.get(0);
        Menu returnMenu = clickEvent.getMenu();
        
        // Get available currencies
        YamlConfig config = CustomPayments.getInstance().getConfig();
        ConfigOption<List<StoreCurrency>> currenciesOption = config.getOption("general.currencies");
        List<StoreCurrency> currencies = currenciesOption.getValue(config);

        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(Material.SUNFLOWER);

            // Set category name
            item.setName(UI.color(currency.getCode(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.addLore("", UI.color("Use left-click to select.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Add glow if selected 
            if(currency.equals(cart.getCurrency())) {
                item.addEnchantGlow();
            }
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                
                // Update order
                cart.setCurrency(currency);
                
                // Update UI
                item.setLore(UI.color("Saving...", UI.COLOR_TEXT));
                menu.disableButtons();
                menu.refresh();

                // Save cart
                orderService.saveOrder(cart, success -> {
                    menu.enableButtons();
                    
                    if (success) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        returnMenu.open(player);
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        item.setLore(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                        menu.refresh();
                    }
                });
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            returnMenu.open(player);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
