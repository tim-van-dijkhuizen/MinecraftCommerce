package nl.timvandijkhuizen.commerce.menu.content.shop;

import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.content.actions.shop.ActionShopProducts;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.StructuredMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopCategories implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        int[] buttonSlots = new int[] { 10, 12, 14, 16, 28, 30, 32, 34 };
        StructuredMenu menu = new StructuredMenu("Shop " + Icon.ARROW_RIGHT + " Categories", MenuSize.XXL, buttonSlots, 1, 5, 7);

        // Add category buttons
        Set<Category> categories = args.getSet(0);

        for (Category category : categories) {
            MenuItemBuilder item = new MenuItemBuilder(category.getIcon());

            // Set category name
            item.setName(UI.color(category.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(category.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }

            // Set click listener
            item.setClickListener(new ActionShopProducts(category));

            menu.addStructuredButton(item);
        }

        // Close button
        MenuItemBuilder closeButton = MenuItems.CLOSE.clone();

        closeButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);
        });

        menu.setButton(closeButton, menu.getSize().getSlots() - 9 + 3);
        
        // Currency button
        menu.setButton(ShopHelper.createCartItem(player), menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}
