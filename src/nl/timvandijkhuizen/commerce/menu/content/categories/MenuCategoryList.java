package nl.timvandijkhuizen.commerce.menu.content.categories;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuCategoryList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        CategoryService categoryService = Commerce.getInstance().getService(CategoryService.class);
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Categories", 3, 7, 1, 1);

        // Add category buttons
        Set<Category> categories = args.getSet(0);

        for (Category category : categories) {
            MenuItemBuilder item = new MenuItemBuilder(category.getIcon());

            // Set category name
            item.setName(UI.color(category.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = ShopHelper.parseDescription(category.getDescription());

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }

            item.addLore("", UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);

                if (clickType == ClickType.LEFT) {
                    Menus.CATEGORY_EDIT.open(player, category);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    categoryService.deleteCategory(category, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedItem(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete category.", UI.COLOR_ERROR));
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

        // Create new category button
        MenuItemBuilder createButton = new MenuItemBuilder(XMaterial.NETHER_STAR);

        createButton.setName(UI.color("Create Category", UI.COLOR_SECONDARY, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CATEGORY_EDIT.open(player);
        });

        menu.setItem(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
