package nl.timvandijkhuizen.commerce.menu.content.gateways;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.sources.GatewayConfig;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
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

public class MenuGatewayList implements PredefinedMenu {

    @Override
    public Menu create(Player player, MenuArguments args) {
        GatewayService gatewayService = Commerce.getInstance().getService("gateways");
        PagedMenu menu = new PagedMenu("Gateways", 3, 7, 1, 1);

        for (Gateway gateway : gatewayService.getGateways()) {
            MenuItemBuilder item = new MenuItemBuilder(Material.OAK_FENCE_GATE);
            Collection<ConfigOption<?>> options = gateway.getType().getOptions();

            // Set gateway name
            item.setName(UI.color(gateway.getDisplayName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            item.setLore(UI.color("Type: ", UI.COLOR_TEXT) + UI.color(gateway.getType().getName(), UI.COLOR_SECONDARY), "");
            
            // Add configuration to lore
            item.addLore(UI.color("Configuration:", UI.COLOR_TEXT));
            
            if(options.size() > 0) {
                for(ConfigOption<?> option : options) {
                    GatewayConfig config = gateway.getConfig();
                    ConfigIcon icon = option.getIcon();
                    
                    // Ignore options without an icon
                    if(icon == null) {
                        continue;
                    }
                    
                    if(!option.isValueEmpty(config)) {
                        item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color(option.getValueLore(config), UI.COLOR_SECONDARY));
                    } else {
                        item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + icon.getName() + ": ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                    }
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Use left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Use right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);
                
                if (clickType == ClickType.LEFT) {
                    Menus.GATEWAY_EDIT.open(player, gateway);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    gatewayService.deleteGateway(gateway, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedButton(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete gateway.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
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

        // Create new gateway button
        MenuItemBuilder createButton = new MenuItemBuilder(Material.NETHER_STAR);

        createButton.setName(UI.color("Create Gateway", UI.COLOR_SECONDARY, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.GATEWAY_EDIT.open(player);
        });

        menu.setButton(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}
