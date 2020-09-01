package nl.timvandijkhuizen.custompayments.menu.content.shop;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.elements.Product;
import nl.timvandijkhuizen.custompayments.helpers.ShopHelper;
import nl.timvandijkhuizen.custompayments.menu.content.actions.OpenShopCategories;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopProducts implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Category category = args[0].as(Category.class);
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " " + category.getName(), 3, 7, 1, 1, 1, 5, 7);

        // Add product buttons
        List<Product> products = args[1].asList(Product.class);

        for (Product product : products) {
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon());
            
            // Set product name
            item.setName(UI.color(product.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(product.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.TEXT_COLOR));
            }
            
            item.addLore(UI.color("Price: ", UI.TEXT_COLOR) + UI.color(ShopHelper.localize(product.getPrice()), UI.SECONDARY_COLOR), "");

            // Set click listener
            item.setClickListener(event -> { });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(new OpenShopCategories());

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Currency button
        menu.setButton(ShopHelper.createCartItem(player, menu), menu.getSize().getSlots() - 9 + 4);
        
        return menu;
    }

}
