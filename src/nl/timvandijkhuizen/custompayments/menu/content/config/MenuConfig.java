package nl.timvandijkhuizen.custompayments.menu.content.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuConfig implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        PagedMenu menu = new PagedMenu("Configuration", 3, 7, 1, 1, 1, 5, 7);

        // Add config options
        for (ConfigOption option : config.getOptions()) {
            ConfigIcon icon = option.getIcon();
            
            // Ignore options without an icon
            if(icon == null) {
                continue;
            }
            
            // Create and add option
            MenuItemBuilder item = new MenuItemBuilder(icon.getMaterial());
            
            item.setName(UI.color(icon.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            
            if(!option.isValueEmpty(config)) {
                item.setLore(UI.color(option.getValueLore(config), UI.SECONDARY_COLOR));
            } else {
                item.setLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }
            
            if(option.isReadOnly()) {
                item.addLore("", UI.color("This option cannot be changed from the GUI.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            } else {
                item.addLore("", UI.color("Left-click to edit this setting.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
                item.addEnchantGlow();
            }
            
            // Set click listener
            if(!option.isReadOnly()) {
                item.setClickListener(event -> {
                    UI.playSound(player, UI.CLICK_SOUND);
                    
                    option.getValueInput(player, value -> {
                        option.setValue(config, value);
                        config.save();
                        
                        // Update menu and open it
                        if(!option.isValueEmpty(config)) {
                            item.setLore(UI.color(option.getValueLore(config), UI.SECONDARY_COLOR), 0);
                        } else {
                            item.setLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC), 0);
                        }
                        
                        menu.open(player);
                    });
                });
            }
            
            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
