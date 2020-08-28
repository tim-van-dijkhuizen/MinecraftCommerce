package nl.timvandijkhuizen.custompayments.menu.content.gateways;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.base.GatewayConfig;
import nl.timvandijkhuizen.custompayments.elements.Gateway;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
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
            
            item.setName(UI.color(icon.getName(), UI.PRIMARY_COLOR, ChatColor.BOLD));
            
            if(!option.isValueEmpty(config)) {
                item.setLore(UI.color(option.getValueLore(config), UI.SECONDARY_COLOR));
            } else {
                item.setLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            }
            
            item.addLore("", UI.color("Use left-click to edit.", UI.SECONDARY_COLOR, ChatColor.ITALIC));
            
            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.CLICK_SOUND);
                
                option.getValueInput(player, value -> {
                    option.setValue(config, value);
                    
                    // Update menu and open it
                    if(!option.isValueEmpty(config)) {
                        item.setLore(UI.color(option.getValueLore(config), UI.SECONDARY_COLOR), 0);
                    } else {
                        item.setLore(UI.color("None", UI.SECONDARY_COLOR, ChatColor.ITALIC), 0);
                    }
                    
                    menu.open(player);
                });
            });
            
            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = Menu.BACK_BUTTON.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.CLICK_SOUND);
            Menus.GATEWAY_EDIT.open(player, gateway);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
