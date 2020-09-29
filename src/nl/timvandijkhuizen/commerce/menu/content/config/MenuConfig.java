package nl.timvandijkhuizen.commerce.menu.content.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuConfig implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Commerce plugin = Commerce.getInstance();
        PagedMenu menu = new PagedMenu("Configuration", 3, 7, 1, 1, 1, 5, 7);
        YamlConfig config = plugin.getConfig();

        // Add configuration options
        for (ConfigOption option : config.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());
            
            // Get meta and read only
            DataArguments meta = option.getMeta();
            boolean restart = meta.getBoolean(0, false);
            
            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLore(() -> {
                List<String> lore = new ArrayList<>();
                
                if(!option.isValueEmpty(config)) {
                    lore.add(UI.color(option.getValueLore(config), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }

                if(restart) {
                    lore.add("");
                    lore.add(UI.color("Warning:", UI.COLOR_ERROR, ChatColor.BOLD) + " " + UI.color("This option requires a restart.", UI.COLOR_ERROR));
                }
                
                lore.add("");
                lore.add(UI.color("Left-click to edit this setting.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                
                return lore;
            });
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                
                option.getValueInput(config, player, value -> {
                    option.setValue(config, value);
                    menu.open(player);
                });
            });
            
            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
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

            // Save product
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                config.save();
                
                MainThread.execute(() -> {
                    plugin.reload();
                    
                    // Check for errors
                    Map<String, String> errors = plugin.getServiceErrors();
                    
                    if(errors.isEmpty()) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        saveButton.setLore(UI.color("Successfully saved config.", UI.COLOR_SUCCESS));
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        saveButton.setLore(UI.color("Failed to save config.", UI.COLOR_ERROR));
                        saveButton.addLore("", UI.color("Errors:", UI.COLOR_ERROR));
                        
                        for(Entry<String, String> error : errors.entrySet()) {
                            saveButton.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + error.getKey() + ": " + error.getValue(), UI.COLOR_ERROR));
                        }
                    }
                    
                    menu.enableButtons();
                    menu.refresh();
                });
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 4);
        
        return menu;
    }

}
