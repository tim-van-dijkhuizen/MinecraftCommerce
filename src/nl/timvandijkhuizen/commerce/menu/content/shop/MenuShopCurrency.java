package nl.timvandijkhuizen.commerce.menu.content.shop;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCurrency implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Currency", 3, 7, 1, 1, 1, 5, 7);
        OrderService orderService = Commerce.getInstance().getService("orders");

        // Get cart & return menu
        Order cart = args.get(0);
        Menu returnMenu = args.get(1);

        // Get available currencies
        YamlConfig config = Commerce.getInstance().getConfig();
        List<StoreCurrency> currencies = config.getOptionValue("general.currencies");

        for (StoreCurrency currency : currencies) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.SUNFLOWER);

            // Set category name
            item.setName(UI.color(currency.getCode().getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.addLore("", UI.color("Left-click to select currency.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Add glow if selected
            if (currency.equals(cart.getCurrency())) {
                item.addEnchantGlow();
            }

            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);

                // Update order
                cart.setCurrency(currency);

                // Update UI
                item.setLore(UI.color("Saving...", UI.COLOR_TEXT));
                menu.disableItems();
                menu.refresh();

                // Save cart
                orderService.saveOrder(cart, success -> {
                    menu.enableItems();

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

            menu.addPagedItem(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            returnMenu.open(player);
        });

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
