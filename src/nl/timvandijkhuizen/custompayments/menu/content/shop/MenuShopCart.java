package nl.timvandijkhuizen.custompayments.menu.content.shop;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.base.ProductSnapshot;
import nl.timvandijkhuizen.custompayments.elements.LineItem;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenShopCategories;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCart implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        OrderService orderService = CustomPayments.getInstance().getService("orders");
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Cart", 3, 7, 1, 1, 2, 5, 6);
        Order cart = orderService.getCart(player);

        for (LineItem lineItem : cart.getLineItems()) {
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

            menu.addPagedButton(item);
        }

        // Cart button
        menu.setButton(ShopHelper.createCartItem(player), menu.getSize().getSlots() - 9 + 3);

        // Home button
        MenuItemBuilder homeButton = MenuItems.BACK.clone();

        homeButton.setName(UI.color("Shop Home", UI.COLOR_SECONDARY, ChatColor.BOLD));
        homeButton.setClickListener(new OpenShopCategories());

        menu.setButton(homeButton, menu.getSize().getSlots() - 9 + 4);
        
        return menu;
    }

}
