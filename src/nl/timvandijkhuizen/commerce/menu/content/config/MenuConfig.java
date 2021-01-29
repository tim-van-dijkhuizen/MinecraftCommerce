package nl.timvandijkhuizen.commerce.menu.content.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.helpers.ThreadHelper;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuConfig implements PredefinedMenu {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Menu create(Player player, DataArguments args) {
        Commerce plugin = Commerce.getInstance();
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Configuration", 3, 7, 1, 1, 1, 5, 7);
        YamlConfig config = plugin.getConfig();

        // Add configuration options
        for (ConfigOption option : config.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());

            // Get meta and read only
            DataArguments meta = option.getMeta();
            boolean restart = meta.getBoolean(0, false);

            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.hideAttributes();

            item.setLoreGenerator(() -> {
                List<String> lore = new ArrayList<>();

                if (!option.isValueEmpty(config)) {
                    lore.add(UI.color(option.getDisplayValue(config), UI.COLOR_TEXT));
                } else {
                    lore.add(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
                }

                if (restart) {
                    lore.add("");
                    lore.add(UI.color("Warning:", UI.COLOR_ERROR, ChatColor.BOLD) + " " + UI.color("This option requires a restart.", UI.COLOR_ERROR));
                }

                lore.add("");
                lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                lore.add(UI.color("Right-click to reset.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

                return lore;
            });

            // Set click listener
            item.setClickListener(event -> {
                ClickType type = event.getClickType();

                if (type == ClickType.LEFT) {
                    UI.playSound(player, UI.SOUND_CLICK);

                    option.getValueInput(config, event, value -> {
                        option.setValue(config, value);
                        menu.open(player);
                    });
                } else if (type == ClickType.RIGHT) {
                    UI.playSound(player, UI.SOUND_DELETE);
                    option.resetValue(config);
                    menu.refresh();
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

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableItems();
            menu.refresh();

            // Save configuration
            ThreadHelper.executeAsync(() -> config.save(), () -> {
                plugin.reload();

                // Check for errors
                Map<String, String> errors = plugin.getServiceErrors();

                if (errors.isEmpty()) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.setLore(UI.color("Successfully saved config.", UI.COLOR_SUCCESS));
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Failed to save config.", UI.COLOR_ERROR));
                    saveButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR));

                    for (Entry<String, String> error : errors.entrySet()) {
                        saveButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error.getKey() + ": " + error.getValue(), UI.COLOR_ERROR));
                    }
                }

                menu.enableItems();
                menu.refresh();
            });
        });

        menu.setItem(saveButton, menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}
