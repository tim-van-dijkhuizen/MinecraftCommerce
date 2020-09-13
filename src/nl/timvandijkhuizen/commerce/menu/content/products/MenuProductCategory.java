package nl.timvandijkhuizen.commerce.menu.content.products;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductCategory implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        PagedMenu menu = new PagedMenu("Product Category", 3, 7, 1, 1, 1, 5, 7);
        Product product = args.get(0);
        Category selected = product.getCategory();

        // Add category buttons
        for (Category category : categoryService.getCategories()) {
            MenuItemBuilder item = new MenuItemBuilder(category.getIcon());

            item.setName(UI.color(category.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (selected != null && category.getId().equals(selected.getId())) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                product.setCategory(category);
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.PRODUCT_EDIT.open(player, product);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player, product);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}