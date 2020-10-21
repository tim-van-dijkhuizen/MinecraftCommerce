package nl.timvandijkhuizen.commerce.menu.content.orders;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        OrderService orderService = Commerce.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Orders", 3, 7, 1, 1, 1, 5, 7);

        // Add order buttons
        Set<Order> orders = args.getSet(0);

        for (Order order : orders) {
            MenuItemBuilder item = new MenuItemBuilder(XMaterial.WRITABLE_BOOK);

            // Set order name
            item.setName(UI.color(order.getUniqueId().toString(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.setLore(UI.color("UUID: ", UI.COLOR_TEXT) + UI.color(order.getPlayerUniqueId().toString(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Username: ", UI.COLOR_TEXT) + UI.color(order.getPlayerName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Currency: ", UI.COLOR_TEXT) + UI.color(order.getCurrency().getCode(), UI.COLOR_SECONDARY));

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
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete order.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
