package nl.timvandijkhuizen.commerce.menu.content.shop.account;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.sources.UserPreferences;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.UserService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopPreferences implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        UserService userService = Commerce.getInstance().getService("users");
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Preferences", 3, 7, 1, 1, 1, 5, 7);
        UserPreferences preferences = userService.getPreferences(player);

        // Add configuration options
        for (ConfigOption option : preferences.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());

            // Get meta data
            DataArguments meta = option.getMeta();
            String description = meta.getString(0);

            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                List<String> lore = new ArrayList<>();

                if(description != null) {
                    lore.add(UI.color(description, UI.COLOR_TEXT));
                    lore.add("");
                }
                
                if (!option.isValueEmpty(preferences)) {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color(option.getDisplayValue(preferences), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }

                lore.add("");
                lore.add(UI.color("Left-click to edit this preference.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                lore.add(UI.color("Right-click to reset this preference.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

                return lore;
            });

            // Set click listener
            item.setClickListener(event -> {
                ClickType type = event.getClickType();

                if (type == ClickType.LEFT) {
                    UI.playSound(player, UI.SOUND_CLICK);

                    option.getValueInput(preferences, event, value -> {
                        option.setValue(preferences, value);
                        menu.open(player);
                    });
                } else if (type == ClickType.RIGHT) {
                    UI.playSound(player, UI.SOUND_DELETE);
                    option.resetValue(preferences);
                    menu.refresh();
                }
            });

            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.SHOP_ACCOUNT.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Save configuration
            userService.savePreferences(player, preferences, success -> {
                menu.enableButtons();

                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.removeLore();
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                }
                
                menu.refresh();
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}
