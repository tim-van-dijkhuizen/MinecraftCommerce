package nl.timvandijkhuizen.commerce.menu.content.categories;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionCategoryList;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.InputHelper;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuCategoryEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        Category category = args.get(0, new Category());
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " " + (category.getId() != null ? "Edit" : "Create") + " Category", MenuSize.LG);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder();

        iconButton.setTypeGenerator(() -> category.getIcon());
        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        iconButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            ValidationHelper.addErrorLore(lore, category, "icon");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CATEGORY_ICON.open(player, category, menu);
        });

        menu.setItem(iconButton, 11);

        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        nameButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (category.getName().length() > 0) {
                lore.add(UI.color(category.getName(), UI.COLOR_TEXT));
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, category, "name");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        nameButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);
            
            InputHelper.getString(player, UI.color("What should be the name of the category?", UI.COLOR_PRIMARY), (ctx, input) -> {
                category.setName(input);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(nameButton, 13);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(XMaterial.PAPER);

        descriptionButton.setName(UI.color("Description", UI.COLOR_PRIMARY, ChatColor.BOLD));

        descriptionButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (category.getDescription().length() > 0) {
                String[] lines = WordUtils.wrap(category.getDescription(), 40).split("\n");

                for (String line : lines) {
                    lore.add(UI.color(line, UI.COLOR_TEXT));
                }
            } else {
                lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, category, "description");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        descriptionButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            menu.close(player);

            InputHelper.getString(player, UI.color("What should be the description of the category?", UI.COLOR_PRIMARY), (ctx, input) -> {
                category.setDescription(input);
                menu.open(player);
                return null;
            });
        });

        menu.setItem(descriptionButton, 15);

        // Set bottom line
        // ===========================
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new ActionCategoryList());

        menu.setItem(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Save category
            categoryService.saveCategory(category, success -> {
                menu.enableItems();

                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.setLore("");
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Error: Field contains an invalid value.", UI.COLOR_ERROR));
                }
                
                menu.refresh();
            });
        });

        menu.setItem(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
