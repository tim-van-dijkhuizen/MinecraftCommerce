package nl.timvandijkhuizen.commerce.menu.content.gateways;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuArguments;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuGatewayOptions implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        PagedMenu menu = new PagedMenu("Gateway Options", 3, 7, 1, 1, 1, 5, 7);
        Gateway gateway = args.get(0);
        GatewayConfig config = gateway.getConfig();

        // Add configuration options
        for (ConfigOption option : config.getOptions()) {
            ConfigIcon icon = option.getIcon();
            
            // Ignore options without an icon
            if(icon == null) {
                continue;
            }
            
            // Create and add option
            MenuItemBuilder item = new MenuItemBuilder(icon.getMaterial());
            
            item.setName(UI.color(icon.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            if(!option.isValueEmpty(config)) {
                for(String line : option.getValueLore(config)) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color(line, UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
            
            item.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                
                option.getValueInput(player, option.getValue(config), value -> {
                    option.setValue(config, value);
                    
                    // Clear lore
                    item.removeLore();
                    
                    // Set new lore
                    if(!option.isValueEmpty(config)) {
                        for(String line : option.getValueLore(config)) {
                            item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color(line, UI.COLOR_SECONDARY));
                        }
                    } else {
                        item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                    }
                    
                    item.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                    
                    // Open menu
                    menu.open(player);
                });
            });
            
            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_EDIT.open(player, gateway);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
