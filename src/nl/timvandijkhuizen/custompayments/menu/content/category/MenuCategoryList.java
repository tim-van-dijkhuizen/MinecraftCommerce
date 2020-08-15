package nl.timvandijkhuizen.custompayments.menu.content.category;

import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.elements.Category;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuCategoryList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        CategoryService categoryService = CustomPayments.getInstance().getService("categories");
        PagedMenu menu = new PagedMenu("Product Categories", 3, 7, 1, 1, 1, 7);

        // Add category buttons
        List<Category> categories = args[0].asList(Category.class);

        for (Category category : categories) {
            MenuItemBuilder item = new MenuItemBuilder(Material.CHEST_MINECART);

            // Set category name
            item.setName(UI.color(category.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = WordUtils.wrap(category.getDescription(), 40).split("\n");

            for (String line : lines) {
                item.addLore(UI.color(line, UI.TEXT_COLOR));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.SECONDARY_COLOR, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                Player whoClicked = event.getPlayer();
                ClickType clickType = event.getClickType();

                if (clickType == ClickType.LEFT) {
                    whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    Menus.CATEGORY_EDIT.open(player, category);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.TEXT_COLOR));
                    menu.refresh();

                    categoryService.deleteCategory(category, success -> {
                        if (success) {
                            whoClicked.playSound(whoClicked.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            item.setLore(UI.color("Error: Failed to delete category.", UI.ERROR_COLOR));
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
            Player whoClicked = event.getPlayer();

            whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.HOME.open(whoClicked);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new category button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Category", UI.SECONDARY_COLOR, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            Player whoClicked = event.getPlayer();

            whoClicked.playSound(whoClicked.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            Menus.CATEGORY_EDIT.open(whoClicked);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
