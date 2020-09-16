package nl.timvandijkhuizen.commerce.menu.content.config;

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
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuConfig implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        Commerce plugin = Commerce.getInstance();
        PagedMenu menu = new PagedMenu("Configuration", 3, 7, 1, 1, 1, 5, 7);
        YamlConfig config = plugin.getConfig();

        // Add config options
        for (ConfigOption option : config.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());
            
            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            if(!option.isValueEmpty(config)) {
                item.addLore(UI.color(option.getValueLore(config), UI.COLOR_SECONDARY));
            } else {
                item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
            
            if(option.isReadOnly()) {
                item.addLore("", UI.color("This option cannot be changed from the GUI.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            } else {
                item.addLore("", UI.color("Left-click to edit this setting.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
            
            // Set click listener
            item.setClickListener(event -> {
                if(!option.isReadOnly()) {
                    UI.playSound(player, UI.SOUND_CLICK);
                    
                    option.getValueInput(player, option.getValue(config), value -> {
                        option.setValue(config, value);
                        
                        // Clear lore
                        item.removeLore();
                        
                        // Set new lore
                        if(!option.isValueEmpty(config)) {
                            item.addLore(UI.color(option.getValueLore(config), UI.COLOR_SECONDARY));
                        } else {
                            item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                        }
                        
                        if(option.isReadOnly()) {
                            item.addLore("", UI.color("This option cannot be changed from the GUI.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                        } else {
                            item.addLore("", UI.color("Left-click to edit this setting.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                        }
                        
                        // Open menu
                        menu.open(player);
                    });
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                }
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
