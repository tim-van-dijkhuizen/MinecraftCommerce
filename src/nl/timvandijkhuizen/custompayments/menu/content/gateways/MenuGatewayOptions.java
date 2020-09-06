package nl.timvandijkhuizen.custompayments.menu.content.gateways;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.config.sources.GatewayConfig;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuGatewayOptions implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        Gateway gateway = args[0].as(Gateway.class);
        GatewayConfig config = gateway.getConfig();
        PagedMenu menu = new PagedMenu("Gateway Options", 3, 7, 1, 1, 1, 5, 7);

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
