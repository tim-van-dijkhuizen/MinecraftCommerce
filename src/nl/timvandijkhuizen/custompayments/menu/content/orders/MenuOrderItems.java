package nl.timvandijkhuizen.custompayments.menu.content.orders;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.base.ProductSnapshot;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderItems implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        PagedMenu menu = new PagedMenu("Order Items", 3, 7, 1, 1, 1, 5, 7);
        Order order = args[0].as(Order.class);

        for (LineItem lineItem : order.getLineItems()) {
            ProductSnapshot product = lineItem.getProduct();
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon(), lineItem.getQuantity());
            
            // Set product name
            item.setName(UI.color(product.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.TEXT_COLOR));
            }
            
            // Category and price
            item.addLore("", UI.color("Category: ", UI.TEXT_COLOR) + UI.color(product.getCategoryName(), UI.SECONDARY_COLOR));
            item.addLore(UI.color("Price: ", UI.TEXT_COLOR) + UI.color(ShopHelper.localize(product.getPrice()), UI.SECONDARY_COLOR), "");
            
            // Commands
            item.addLore(UI.color("Commands:", UI.TEXT_COLOR));
            
            if(product.getCommands().size() > 0) {
                for (String command : product.getCommands()) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command, UI.SECONDARY_COLOR));
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.ORDER_VIEW.open(player, order);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
