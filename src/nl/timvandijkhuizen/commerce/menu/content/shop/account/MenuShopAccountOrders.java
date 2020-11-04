package nl.timvandijkhuizen.commerce.menu.content.shop.account;

import java.util.Collection;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopAccountOrders implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Donation History", 3, 7, 1, 1, 1, 5, 7);

        // Add order buttons
        Set<Order> orders = args.getSet(0);

        for (Order order : orders) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

            // Set order name
            item.setName(UI.color(order.getUniqueId().toString(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.addLore(UI.color("Currency: ", UI.COLOR_TEXT) + UI.color(order.getCurrency().getCode().getDisplayName(), UI.COLOR_SECONDARY));

            // Add LineItems
            DataList<LineItem> lineItems = order.getLineItems();

            item.addLore("", UI.color("Items", UI.COLOR_PRIMARY, ChatColor.BOLD));

            if (lineItems.size() > 0) {
                for (LineItem lineItem : lineItems) {
                    ProductSnapshot product = lineItem.getProduct();
                    String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                    String price = ShopHelper.formatPrice(lineItem.getPrice(), order.getCurrency());

                    item.addLore(UI.TAB + UI.color(Icon.SQUARE, UI.COLOR_TEXT) + " " + UI.color(quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.TAB + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            // Add fields
            OrderFieldData fieldData = order.getFieldData();
            Collection<ConfigOption<?>> options = fieldData.getOptions();

            item.addLore("", UI.color("Fields", UI.COLOR_PRIMARY, ChatColor.BOLD));

            if (options.size() > 0) {
                for (ConfigOption<?> option : options) {
                    String value = UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC);

                    if (!option.isValueEmpty(fieldData)) {
                        value = UI.color(option.getDisplayValue(fieldData), UI.COLOR_SECONDARY);
                    }

                    item.addLore(UI.TAB + UI.color(Icon.SQUARE + " " + option.getName() + ": ", UI.COLOR_TEXT) + value);
                }
            } else {
                item.addLore(UI.TAB + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_ACCOUNT.open(player);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
