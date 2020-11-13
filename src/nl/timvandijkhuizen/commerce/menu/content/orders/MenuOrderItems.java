package nl.timvandijkhuizen.commerce.menu.content.orders;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderItems implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Order Items", 3, 7, 1, 1, 1, 5, 7);
        Order order = args.get(0);

        for (LineItem lineItem : order.getLineItems()) {
            ProductSnapshot product = lineItem.getProduct();
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon(), lineItem.getQuantity());

            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }

            // Category and price
            item.addLore("", UI.color("Category: ", UI.COLOR_TEXT) + UI.color(product.getCategoryName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Price: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_SECONDARY), "");

            // Commands
            item.addLore(UI.color("Commands:", UI.COLOR_TEXT));

            if (product.getCommands().size() > 0) {
                for (String command : product.getCommands()) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command, UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.ORDER_VIEW.open(player, order);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
