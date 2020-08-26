package nl.timvandijkhuizen.custompayments.menu.content.orders;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        OrderService orderService = CustomPayments.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Orders", 3, 7, 1, 1, 1, 5, 7);

        // Add order buttons
        List<Order> orders = args[0].asList(Order.class);

        for (Order order : orders) {
            MenuItemBuilder item = new MenuItemBuilder(Material.WRITABLE_BOOK);

            // Set order name
            item.setName(UI.color(order.getReference(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            item.setLore(UI.color("UUID: ", UI.TEXT_COLOR) + UI.color(order.getPlayerUniqueId().toString(), UI.SECONDARY_COLOR));
            item.addLore(UI.color("Username: ", UI.TEXT_COLOR) + UI.color(order.getPlayerName(), UI.SECONDARY_COLOR));
            item.addLore(UI.color("Currency: ", UI.TEXT_COLOR) + UI.color(order.getCurrency().getDisplayName(), UI.SECONDARY_COLOR));

            item.addLore("", UI.color("Use left-click to view.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.CLICK_SOUND);
                
                if (clickType == ClickType.LEFT) {
                    Menus.ORDER_VIEW.open(player, order);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.TEXT_COLOR));
                    menu.refresh();

                    orderService.deleteOrder(order, success -> {
                        if (success) {
                            UI.playSound(player, UI.DELETE_SOUND);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.ERROR_SOUND);
                            item.setLore(UI.color("Error: Failed to delete order.", UI.ERROR_COLOR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
