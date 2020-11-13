package nl.timvandijkhuizen.commerce.menu.content.orders;

import java.util.Collection;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
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

public class MenuOrderList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Orders", 3, 7, 1, 1, 1, 5, 7);

        // Add order buttons
        Set<Order> orders = args.getSet(0);

        for (Order order : orders) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

            // Set order name
            item.setName(UI.color(order.getUniqueId().toString(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.setLore(UI.color("UUID: ", UI.COLOR_TEXT) + UI.color(order.getPlayerUniqueId().toString(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Username: ", UI.COLOR_TEXT) + UI.color(order.getPlayerName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Currency: ", UI.COLOR_TEXT) + UI.color(order.getCurrency().getCode().getDisplayName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Total: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(order.getTotal()), UI.COLOR_SECONDARY));

            // Add LineItems
            DataList<LineItem> lineItems = order.getLineItems();

            item.addLore("", UI.color("Items", UI.COLOR_PRIMARY));

            if (lineItems.size() > 0) {
                for (LineItem lineItem : lineItems) {
                    ProductSnapshot product = lineItem.getProduct();
                    String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                    String price = ShopHelper.formatPrice(lineItem.getPrice());

                    item.addLore(UI.TAB + UI.color(Icon.SQUARE + " " + quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_TEXT));
                }
            } else {
                item.addLore(UI.TAB + UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            // Add fields
            OrderFieldData fieldData = order.getFieldData();
            Collection<ConfigOption<?>> options = fieldData.getOptions();

            item.addLore("", UI.color("Fields", UI.COLOR_PRIMARY));

            if (options.size() > 0) {
                for (ConfigOption<?> option : options) {
                    String value = UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC);

                    if (!option.isValueEmpty(fieldData)) {
                        value = UI.color(option.getDisplayValue(fieldData), UI.COLOR_SECONDARY);
                    }

                    item.addLore(UI.TAB + UI.color(Icon.SQUARE + " " + option.getName() + ": ", UI.COLOR_TEXT) + value);
                }
            } else {
                item.addLore(UI.TAB + UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Left-click to view.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);

                if (clickType == ClickType.LEFT) {
                    Menus.ORDER_VIEW.open(player, order);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    orderService.deleteOrder(order, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedItem(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete order.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
